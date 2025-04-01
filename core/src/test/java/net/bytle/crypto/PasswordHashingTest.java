package net.bytle.crypto;


import org.junit.Test;

public class PasswordHashingTest {

  @Test
  public void passwordHash() {

    String key = PasswordHashing.createFromSalt("salt")
      .toHash("welcome123");
    System.out.println(key);

  }

}
