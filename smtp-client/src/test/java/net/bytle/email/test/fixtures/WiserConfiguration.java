package net.bytle.email.test.fixtures;

import net.bytle.email.BMailSmtpClient;
import net.bytle.email.BMailStartTls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.server.SMTPServer;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The wiser configuration is returned in a builder
 * We just extracts this configuration to get a clean code
 * in the WiserBaseTest test class
 * <p>
 * This is the SMTP server configuration
 * and allows:
 * * TLS
 * * with Authentication
 * <p>
 * based on
 * <a href="https://github.com/davidmoten/subethasmtp/blob/master/src/test/java/org/subethamail/smtp/StartTLSFullTest">StartTLSFullTest</a>java
 */
public class WiserConfiguration {

  private static final Logger log = LoggerFactory.getLogger(WiserConfiguration.class);

  private static final String CREDENTIAL_STORE_PASSWORD = "password";
  public final static int WISER_PORT = 1081;
  public static final BMailStartTls START_TLS_VALUE = BMailStartTls.REQUIRE;
  public static final boolean REQUIRE_AUTH = true;

  public static SMTPServer.Builder getBuilderWithoutSslAndAuth() {

    return
      SMTPServer.port(WISER_PORT)
        .enableTLS(false)
        .requireTLS(false)
        .requireAuth(false);


  }

  public static SMTPServer.Builder getSslBuilder() {

    /**
     * To enable a TLS connection
     */

    try {
      KeyManager[] keyManagers = getKeyManagers();
      TrustManager[] trustManagers = getTrustManagers();
      SSLContext sslContext = createTlsSslContext(keyManagers, trustManagers);
      return
        SMTPServer.port(WISER_PORT)
          .enableTLS(START_TLS_VALUE.getEnableStartTls())
          .requireTLS(START_TLS_VALUE.getRequireStartTls())
          .requireAuth(REQUIRE_AUTH)
          .startTlsSocketFactory(sslContext)
          .authenticationHandlerFactory(
            new AuthenticationHandlerFactory() {

              @Override
              public List<String> getAuthenticationMechanisms() {
                return Collections.singletonList("PLAIN");
              }

              @Override
              public AuthenticationHandler create() {
                return new AuthenticationHandler() {

                  @Override
                  public Optional<String> auth(String clientInput, MessageContext context) {
                    log.info(clientInput);
                    return Optional.empty();
                  }

                  @Override
                  public Object getIdentity() {
                    return "username";
                  }
                };
              }
            });

    } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException |
             UnrecoverableKeyException | KeyManagementException e) {
      throw new RuntimeException(e);
    }


  }

  static SSLContext createTlsSslContext(KeyManager[] keyManagers, TrustManager[] trustManagers)
    throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManagers, trustManagers, new java.security.SecureRandom());
    return sslContext;
  }

  static TrustManager[] getTrustManagers() {
    InputStream trustStore = WiserConfiguration.class.getResourceAsStream("/trustStore.jks");
    TrustManager trustManager = new WiserTrustManager(trustStore, CREDENTIAL_STORE_PASSWORD.toCharArray(), false);
    return new TrustManager[]{trustManager};
  }


  static KeyManager[] getKeyManagers() throws KeyStoreException, IOException, NoSuchAlgorithmException,
    CertificateException, UnrecoverableKeyException {
    InputStream keyStore = WiserConfiguration.class.getResourceAsStream("/keyStore.jks");
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(keyStore, CREDENTIAL_STORE_PASSWORD.toCharArray());
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(ks, CREDENTIAL_STORE_PASSWORD.toCharArray());
    return kmf.getKeyManagers();
  }

  public static BMailSmtpClient getSession() {
    return BMailSmtpClient.create()
      .setPort(WiserConfiguration.WISER_PORT)
      .setUsername("foo")
      .setPassword("bar")
      .setStartTls(START_TLS_VALUE)
      .build();
  }
}
