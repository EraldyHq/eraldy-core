package net.bytle.type;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;


public class Bytes {

  public static String toBase64(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  /**
   * No = at the end
   */
  public static String toBase64UrlWithoutPadding(byte[] bytes) {
    return java.util.Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(bytes);
  }

  /**
   * A wrapper around {@link Byte#parseByte(String)}
   *
   */
  @SuppressWarnings("unused")
  Byte fromString(String s) {
    return Byte.parseByte(s);
  }

  /**
   * hexadecimal digits
   */
  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

  static public String toHexaDecimalViaStringBuilder(byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : bytes) {
      stringBuilder.append(String.format("%02X", b));
    }
    return stringBuilder.toString();
  }

  // hexadecimal representation of the binary data.
  // hexlify
  // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
  //
  //
  static public String toHexaDecimalViaMap(byte[] bytes) {

    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);

  }


  /**
   * Byte to UTF-8 String
   *
   * @return an UTF-8 String
   */
  static public String toString(byte[] bytes) {
    // Arrays.toString(bytes)
    return new String(bytes, StandardCharsets.UTF_8);
  }

  /**
   * @return a random generated byte array of length
   */
  static public byte[] getRandomBytes(int length) {
    byte[] bytes = new byte[length];
    new Random().nextBytes(bytes);
    return bytes;
  }

  /**
   * @return the hexadecimal representation of the binary data. (Hexlify in Python)
   * Every byte of data is converted into the corresponding 2-digit hex representation. The resulting string is therefore twice as long as the length of data.
   */
  public static String toHexaDecimal(byte[] bytes) {
    return toHexaDecimalViaMap(bytes);
  }

  /**
   * @return the hexadecimal representation of the binary data. (Hexlify in Python)
   * <p>
   * javax.xml.bind.DatatypeConverter.printHexBinary(bytes); is deprecated in 9/10 and removed in 11
   */
  public static String printHexBinary(byte[] bytes) {
    return toHexaDecimalViaMap(bytes);
  }


  public static boolean equals(byte[] bytes1, byte[] bytes2) {
    if (bytes1.length != bytes2.length) {
      return false;
    }
    for (int i = 0; i < bytes1.length; i++) {
      if (bytes1[i] != bytes2[i]) {
        return false;
      }
    }
    return true;
  }
}
