
package net.bytle.email.test.fixtures;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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


  @BeforeClass
  public static void beforeClass()  {

    wiser = Wiser.create(WiserConfiguration.getSslBuilder());
    wiser.start();

  }


  @AfterClass
  public static void afterClass() {
    if (wiser != null) {
      wiser.stop();
    }
  }


}
