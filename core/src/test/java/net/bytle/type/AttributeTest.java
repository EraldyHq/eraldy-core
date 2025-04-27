package net.bytle.type;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class AttributeTest {

  @Test
  public void setTest() {

    Variable<String> variable = Variable.createWithClass("blue", Origin.INTERNAL, String.class);
    Variable<String> variable2 = Variable.create(AttributeEnumForTest.BLUE, Origin.INTERNAL);
    Assert.assertEquals(variable, variable2);
    Set<Variable<String>> attributes = new HashSet<>();
    attributes.add(variable);
    attributes.add(variable2);
    Assert.assertEquals(1, attributes.size());

  }

}
