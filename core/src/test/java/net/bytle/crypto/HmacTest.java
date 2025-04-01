package net.bytle.crypto;

import net.bytle.type.Bytes;
import org.junit.Assert;
import org.junit.Test;

public class HmacTest {

  @Test
  public void hmacTest() {

    final String passphrase = "master";
    final String message = "My message";
    CryptoHmac hmac = CryptoHmac.create(passphrase);
    final byte[] digest = hmac.encrypt(message);

    System.out.println("Digest: " + Bytes.toString(digest));

    hmac = CryptoHmac.create(passphrase);
    byte[] digest2 = hmac.encrypt(message);
    Assert.assertArrayEquals("digest are the same", digest, digest2);

  }
}
