package net.bytle.type;

import org.junit.Assert;
import org.junit.Test;

public class KeyNormalizerTest {

    @Test
    public void testEquals() {

        boolean equals = KeyNormalizer.create("hallo foo bar").equals(KeyNormalizer.create("HalloFooBar"));
        Assert.assertTrue(equals);

        equals = KeyNormalizer.create("hallo-foo _bar").equals(KeyNormalizer.create("HalloFooBar"));
        Assert.assertTrue(equals);

        equals = KeyNormalizer.create("hallo-foo _bar").equals(KeyNormalizer.create("Hallo Foo Bar"));
        Assert.assertTrue(equals);

    }

}