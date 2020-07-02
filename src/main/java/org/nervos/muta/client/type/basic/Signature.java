package org.nervos.muta.client.type.basic;

import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.util.Util;

public class Signature {
  @NonNull private final byte[] sig;

  public Signature(byte[] sig) {
    if (is_invalid(sig)) {
      throw new RuntimeException("Signature's byte[] length is not 64 bytes");
    }
    this.sig = sig;
  }

  public Signature(String sigHex) {
    byte[] sig = Hex.decode(Util.remove0x(sigHex));

    if (is_invalid(sig)) {
      throw new RuntimeException("Signature's byte[] length is not 64 bytes");
    }
    this.sig = sig;
  }

  public static boolean is_invalid(byte[] input) {
    return input.length != 64;
  }

  public byte[] getBytes() {
    return sig;
  }

  @Override
  public String toString() {
    return "Signature{" + Util.start0x(Hex.toHexString(this.sig)) + '}';
  }
}
