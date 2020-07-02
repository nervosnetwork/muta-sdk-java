package org.nervos.muta.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

public class Util {

  public static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public static byte[] keccak256(byte[] input) {

    Keccak.DigestKeccak kecc = new Keccak.Digest256();
    kecc.update(input);
    return kecc.digest();
  }

  public static byte[] generateRandom32Bytes() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return bytes;
  }

  public static String generateRandom32BytesHex() {
    return "0x" + Hex.toHexString(generateRandom32Bytes());
  }

  public static String start0x(String input) {
    if (!input.startsWith("0x")) {
      return "0x" + input;
    }
    return input;
  }

  public static String remove0x(String input) {
    if (input.startsWith("0x")) {
      return input.substring(2);
    }
    return input;
  }

  public static void isValidHex(String input) {
    if (!input.startsWith("0x")) {
      throw new RuntimeException("Hex String should have 0x prefix");
    }
  }

  public static byte[] bigIntegerToBytes32(BigInteger input) {
    if (input.signum() <= 0) {
      throw new RuntimeException("bigIntegerToBytes32, input is a negative BigInteger");
    }

    byte[] src = input.toByteArray();
    byte[] dest = new byte[32];
    boolean isFirstByteOnlyForSign = src[0] == 0;
    int length = isFirstByteOnlyForSign ? src.length - 1 : src.length;

    if (length > 32) {
      throw new RuntimeException("bigIntegerToBytes32, input is to long, more than 32 bytes");
    }

    int srcPos = isFirstByteOnlyForSign ? 1 : 0;
    int destPos = 32 - length;
    System.arraycopy(src, srcPos, dest, destPos, length);
    return dest;
  }
}
