package net.bytle.type;

import net.bytle.exception.NoValueException;
import org.junit.Assert;
import org.junit.Test;

public class VariableTest {


  @Test
  public void testDefaultValueProvider() throws NoValueException {


    String yolo = "Yolo";
    Variable name = Variable.create("name", Origin.INTERNAL)
      .setValueProvider(() -> yolo);

    Assert.assertEquals(yolo, name.getValue());

  }

}
