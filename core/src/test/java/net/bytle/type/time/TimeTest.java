package net.bytle.type.time;

import org.junit.Assert;
import org.junit.Test;




public class TimeTest {

  @Test
  public void timeTest() {
    Time time = Time.createFromString("22:10:22");
    java.sql.Time expected = java.sql.Time.valueOf("22:10:22");
    Assert.assertEquals("Same time",expected,time.toSqlTime());
  }
}
