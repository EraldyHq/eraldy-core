package net.bytle.type;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class AttributeTest {

  @Test
  public void setTest() {

    Variable variable = Variable.createWithClass("blue", Origin.INTERNAL, String.class);
    Variable variable2 = Variable.create(AttributeEnumForTest.BLUE, Origin.INTERNAL);
    Assert.assertEquals(variable, variable2);
    Set<Variable> attributes = new HashSet<>();
    attributes.add(variable);
    attributes.add(variable2);
    Assert.assertEquals(1, attributes.size());


  }
}
