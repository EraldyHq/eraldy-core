package net.bytle.crypto;

import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.daead.DeterministicAeadConfig;
import net.bytle.type.Bytes;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class TinkTest {

  private static final String MODE_ENCRYPT = "encrypt";
  private static final String MODE_DECRYPT = "decrypt";

  private static final String AES_GCM = "aesgcm";

  /**
   * https://developers.google.com/tink/deterministic-aead#choosing_a_key_type
   * encrypting the same data always yields the same ciphertext.
   * This type of encryption is useful for key wrapping
   * or for some schemes for searching on encrypted data (see RFC 5297, Section 1.3 for more info)
   */
  private static final String AES_SIV = "aessiv";


  @Test
  public void protectionTest() throws GeneralSecurityException, IOException {
    //String input = "12345678";
    String input = "AXJmOD78PsQMJKvMoJiDZl";
    protector(MODE_ENCRYPT, AES_SIV, input);
  }

  /**
   * https://developers.google.com/tink/encrypt-data
   * <p>
   *
   */
  public void protector(String mode, String encryptionType, String inputString) throws GeneralSecurityException, IOException {
    // mode: Can be "encrypt" or "decrypt" to encrypt/decrypt the input to the output

    // key-file: Read the key material from this file.
    // File keyFile = new File(args[1]);
    // input-file: Read the input from this file.
    // File inputFile = new File("file");
    //byte[] input = Files.readAllBytes(inputFile.toPath());
    byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
    // output-file: Write the result to this file.
    // File outputFile = new File(output);
    // FileOutputStream outputStream = new FileOutputStream(outputFile);

    try (PrintStream outputStream = System.out) {
      // [optional] associated-data: Associated data used for the encryption or decryption.
      byte[] associatedData = new byte[0];

      if (encryptionType.equals(AES_GCM)) {
        // Register all AEAD key types with the Tink runtime.
        AeadConfig.register();
      } else {
        //# Register the deterministic AEAD key manager. This is needed to create a
        //# DeterministicAead primitive later.
        // otherwise No wrapper found for com.google.crypto.tink.
        DeterministicAeadConfig.register();
      }


      String keyset;
      if (encryptionType.equals(AES_SIV)) {
        //#A keyset created with "tinkey create-keyset --key-template=AES256_GCM".Note
        //#that this keyset has the secret key information in cleartext.
        keyset = "{\n" +
          "      \"key\": [{\n" +
          "          \"keyData\": {\n" +
          "              \"keyMaterialType\":\n" +
          "                  \"SYMMETRIC\",\n" +
          "              \"typeUrl\":\n" +
          "                  \"type.googleapis.com/google.crypto.tink.AesSivKey\",\n" +
          "              \"value\":\n" +
          "                  \"EkAl9HCMmKTN1p3V186uhZpJQ+tivyc4IKyE+opg6SsEbWQ/WesWHzwCRrlgRuxdaggvgMzwWhjPnkk9gptBnGLK\"\n" +
          "          },\n" +
          "          \"keyId\": 1919301694,\n" +
          "          \"outputPrefixType\": \"TINK\",\n" +
          "          \"status\": \"ENABLED\"\n" +
          "      }],\n" +
          "      \"primaryKeyId\": 1919301694\n" +
          "  }";
      } else {
        // A keyset created with "tinkey create-keyset --key-template=AES256_SIV". Note
        // that this keyset has the secret key information in cleartext.
        // Read the keyset into a KeysetHandle.
        keyset = "{\n" +
          "      \"key\": [{\n" +
          "          \"keyData\": {\n" +
          "              \"keyMaterialType\":\n" +
          "                  \"SYMMETRIC\",\n" +
          "              \"typeUrl\":\n" +
          "                  \"type.googleapis.com/google.crypto.tink.AesGcmKey\",\n" +
          "              \"value\":\n" +
          "                  \"GiBWyUfGgYk3RTRhj/LIUzSudIWlyjCftCOypTr0jCNSLg==\"\n" +
          "          },\n" +
          "          \"keyId\": 294406504,\n" +
          "          \"outputPrefixType\": \"TINK\",\n" +
          "          \"status\": \"ENABLED\"\n" +
          "      }],\n" +
          "      \"primaryKeyId\": 294406504\n" +
          "  }";
      }
      KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withString(keyset));


      // Get the primitive.
      //DeterministicAead aead;
      switch (encryptionType) {
        case AES_GCM:
          Aead aead = handle.getPrimitive(Aead.class);
          // Use the primitive to encrypt/decrypt files.
          if (MODE_ENCRYPT.equals(mode)) {
            byte[] ciphertext = aead.encrypt(input, associatedData);
            outputStream.println(Bytes.toBase64UrlWithoutPadding(ciphertext));
          } else if (MODE_DECRYPT.equals(mode)) {
            byte[] plaintext = aead.decrypt(input, associatedData);
            outputStream.write(plaintext);
          } else {
            throw new RuntimeException("The first argument must be either encrypt or decrypt, got: " + mode);
          }
          break;
        case AES_SIV:
          DeterministicAead aeadDeter = handle.getPrimitive(DeterministicAead.class);
          if (MODE_ENCRYPT.equals(mode)) {
            byte[] ciphertext = aeadDeter.encryptDeterministically(input, associatedData);
            outputStream.println(Bytes.toBase64UrlWithoutPadding(ciphertext));
          } else if (MODE_DECRYPT.equals(mode)) {
            byte[] plaintext = aeadDeter.decryptDeterministically(input, associatedData);
            outputStream.write(plaintext);
          } else {
            throw new RuntimeException("The first argument must be either encrypt or decrypt, got: " + mode);
          }
          break;

      }


    }

  }
}
