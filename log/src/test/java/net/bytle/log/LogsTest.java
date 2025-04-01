package net.bytle.log;

import org.junit.Test;

import java.util.logging.Level;

import static org.junit.Assert.*;

public class LogsTest {


  /**
   * The message in a fine level should
   * change the log format
   * to the {@link Log#EXTENDED_FORMAT} to show the procedure information
   */
  @Test
  public void visualTest() {
    Log logger = Logs.createFromClazz(LogsTest.class);
    Logs.setLevel(Level.FINE);
    logger.fine("fine");
    logger.info("info");
  }

}
