package net.bytle.type;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DoublesTest {

  @Test
  public void toFloat() {
    Float expectedFloat = (float) 1.01;
    Float theFloat = Doubles.createFromFloat(expectedFloat).toFloat();
    Assert.assertEquals("they are the same", expectedFloat, theFloat);
  }

}
