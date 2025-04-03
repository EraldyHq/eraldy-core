package net.bytle.email.test.fixtures;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Used in the {@link WiserConfiguration}
 * to enable TLS
 */
public final class WiserTrustManager extends X509ExtendedTrustManager {

    private final X509TrustManager defaultTm; // cacerts
    private final X509TrustManager customTm;

    public WiserTrustManager(InputStream trustStoreInputStream, char[] trustStorePassword,
                             boolean extend) {
        try {
            if (extend) {
                X509TrustManager defaultTm = null;
                {
                    TrustManagerFactory tmf = TrustManagerFactory
                            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init((KeyStore) null);
                    for (TrustManager tm : tmf.getTrustManagers()) {
                        if (tm instanceof X509TrustManager) {
                            defaultTm = (X509TrustManager) tm;
                            break;
                        }
                    }
                }
                this.defaultTm = defaultTm;
            } else {
                defaultTm = null;
            }
            final KeyStore trustStore;
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(trustStoreInputStream, trustStorePassword);

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            X509TrustManager customTm = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    customTm = (X509TrustManager) tm;
                    break;
                }
            }
            this.customTm = customTm;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        if (defaultTm != null) {
            defaultTm.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        try {
            customTm.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            if (defaultTm != null) {
                defaultTm.checkServerTrusted(chain, authType);
            }
        }

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (defaultTm != null) {
            return defaultTm.getAcceptedIssuers();
        } else
            return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
            throws CertificateException {
        checkClientTrusted(chain, authType);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
            throws CertificateException {
        checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
            throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
            throws CertificateException {
        checkServerTrusted(chain, authType);
    }

}
