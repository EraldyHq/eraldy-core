package net.bytle.niofs.http;

import net.bytle.niofs.ssl.Ssls;
import net.bytle.type.Base64Utility;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Objects;

/**
 * HTTP Static methods
 */
class HttpRequest {


  /**
   * Get a {@link HttpURLConnection HTTP Request}
   * with common http headers such as:
   * * {@link HttpHeader#USER_AGENT}
   * * authentication
   * * ssl
   *
   * @param httpPath - the path request to fetch
   * @return the connection (fetch object, response comes after connect)
   *
   */
  static HttpURLConnection getHttpRequest(HttpPath httpPath) {
    try {

      URL url = httpPath.toUri().toURL();
      URLConnection urlConnection = url.openConnection();
      if (!(urlConnection instanceof HttpURLConnection)) {
        throw new RuntimeException("The URL is not using HTTP/HTTPS: " + url);
      }
      HttpURLConnection connection = (HttpURLConnection) urlConnection;
      if (urlConnection instanceof HttpsURLConnection) {
        // Trust all certificates
        // TODO: implement it as an option
        ((HttpsURLConnection) urlConnection).setSSLSocketFactory(Ssls.getTrustAllCertificateSocketFactory());
      }
      Files.getAttribute(httpPath, HttpHeader.USER_AGENT.toKeyNormalizer().toHttpHeaderCase());
      if (httpPath.getFileSystem().hasPassword()) {
        connection.addRequestProperty(HttpHeader.AUTHORIZATION.toKeyNormalizer().toHttpHeaderCase(), "Basic " + getBasicAuthenticationString(httpPath));
      }
      return connection;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getBasicAuthenticationString(HttpPath httpPath) throws IOException {
    HttpFileSystem fileSystem = httpPath.getFileSystem();
    String user = fileSystem.getUser();
    Objects.requireNonNull(user, "User should be provided for basic authentication string");
    String password = fileSystem.getPassword();
    Objects.requireNonNull(password, "Password should be provided for basic authentication string");
    return Base64Utility.stringToBase64UrlString(user + ":" + password);
  }


}
