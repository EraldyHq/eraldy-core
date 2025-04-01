package net.bytle.crypto;

import net.bytle.type.Bytes;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64UtilityTest {

    @Test
    public void base64Test() {
        final byte[] bytes = Bytes.getRandomBytes(40);
        String encode = Base64.getEncoder().encodeToString(bytes);
        byte[] decode = Base64.getDecoder().decode(encode);
        System.out.println(new String(decode, StandardCharsets.UTF_8));
        Assert.assertArrayEquals("After decode encode, the arrays must be equals",bytes,decode);

    }

}
