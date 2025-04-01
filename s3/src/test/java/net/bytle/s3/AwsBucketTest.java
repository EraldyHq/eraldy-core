package net.bytle.s3;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.bytle.vertx.ConfigAccessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
class AwsBucketTest {

  @Test
  public void testBadAwsConfig() throws Throwable {

    VertxTestContext testContext = new VertxTestContext();

    ConfigAccessor name = ConfigAccessor.createManually("name", new JsonObject()
      .put("s3_url_endpoint", "https://example.com")
      .put("s3_region", "whatever")
      .put("s3_bucket_name", "unknown")
    );
    Vertx vertx = Vertx.vertx();
    AwsBucket.init(vertx, name)
      .checkConnection()
      .onComplete(testContext.failingThenComplete());

    awaitCompletion(testContext);

  }

  /**
   * To not lose the IDE integration
   * (ie a double click on the test navigates to the method)
   * we handle the completion
   */
  private static void awaitCompletion(VertxTestContext testContext) throws Throwable {
    int timeout = 30;
    Assertions.assertTrue(testContext.awaitCompletion(timeout, TimeUnit.SECONDS), "Should finish in " + timeout + " seconds");
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

}
