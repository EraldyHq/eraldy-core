package net.bytle.type.time;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class DateTest {

  @Test
  public void toSqlDateTest() {

    Date now = Date.createFromNow();
    java.sql.Date localDateNow = java.sql.Date.valueOf(LocalDate.now());
    Assert.assertEquals("equal", now.toSqlDate(), localDateNow);

  }

  @Test
  public void toStringIsoTest() {
    Date date = Date.createFromEpochMilli(System.currentTimeMillis());
    Assert.assertEquals("The iso format is the good one",LocalDate.now().toString(),date.toString("yyyy-MM-dd"));
    Assert.assertEquals("The iso format is the good one",LocalDate.now().toString(),date.toIsoString());
  }


}
