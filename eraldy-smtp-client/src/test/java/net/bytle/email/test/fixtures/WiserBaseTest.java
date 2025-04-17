
package net.bytle.email.test.fixtures;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.wiser.Wiser;

/**
 *
 * Start/stop a dummy test server for each test
 * <p>
 * where authentication and TLS are supported
 * <p>
 * The server is Wiser, this supports inspecting the messages that were received in the test code
 * <p>
 *
 */
public class WiserBaseTest {


  protected static Wiser wiser;


  @BeforeAll
  public static void beforeClass()  {

    SMTPServer.Builder sslBuilder = WiserConfiguration.getBuilderWithoutSslAndAuth();
    wiser = Wiser.create(sslBuilder);
    wiser.start();

  }


  @AfterAll
  public static void afterClass() {
    if (wiser != null) {
      wiser.stop();
    }
  }


}
