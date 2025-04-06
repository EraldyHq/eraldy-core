package net.bytle.test;


public class TestUtil {

  public static void runOnlyIfSlowTestisOn() {
    assert ("true".equals(System.getProperty(TestCategory.SLOW_TEST.toString())));
  }

}
