package net.bytle.type;


import net.bytle.exception.CastException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeyNormalizerTest {

  @Test
  public void testEquals() throws CastException {

    boolean equals = KeyNormalizer.create("hallo foo bar").equals(KeyNormalizer.create("HalloFooBar"));
    Assertions.assertTrue(equals);

    equals = KeyNormalizer.create("hallo-foo _bar").equals(KeyNormalizer.create("HalloFooBar"));
    Assertions.assertTrue(equals);

    equals = KeyNormalizer.create("hallo-foo _bar").equals(KeyNormalizer.create("Hallo Foo Bar"));
    Assertions.assertTrue(equals);

  }

  @Test
  void testCamelCase() throws CastException {

    Assertions.assertEquals(
      "UpperSnakeCase",
      KeyNormalizer.create("UPPER SNAKE CASE").toCamelCase()
      ,"Split only if the previous uppercase is not uppercase");

  }

  @Test
  public void testSqlName() throws CastException {

    boolean equals = KeyNormalizer.create("hallo-foo _bar").toSqlName().equals("hallo_foo__bar");
    Assertions.assertTrue(equals, "bad character as underscore");

    equals = KeyNormalizer.create("HalloFoo").toSqlName().equals("HalloFoo");
    Assertions.assertTrue(equals, "uppercase is valid");

  }

}
