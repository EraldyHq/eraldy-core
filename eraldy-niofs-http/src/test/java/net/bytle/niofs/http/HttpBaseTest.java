package net.bytle.niofs.http;

import net.bytle.test.TestContainerWrapper;
import org.junit.jupiter.api.BeforeEach;

public class HttpBaseTest {

   String httpBinUrl;

  @BeforeEach
  void setUp() {
    if (this.httpBinUrl == null) {
      // .withBindMount(Path.of("src", "test", "resources"), "/httpbin/static/resources")
      // Does not work unfortunately, we can mount
      TestContainerWrapper testContainerWrapper = new TestContainerWrapper("httpbin", "kennethreitz/httpbin:latest")
        .withPort(8085, 80)
        .startContainer();
      this.httpBinUrl = "http://" + testContainerWrapper.getHostName() + ":" + testContainerWrapper.getHostPort();
    }
  }

}
