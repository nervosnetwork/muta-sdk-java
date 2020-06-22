package org.nervos.muta.util;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;

public class Util {

    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static byte[] keccak256(byte[] input) {

        Keccak.DigestKeccak kecc = new Keccak.Digest256();
        kecc.update(input);
        return kecc.digest();
    }

    public static byte[] generateRandom32Bytes(){
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static String generateRandom32BytesHex(){
        return "0x"+Hex.toHexString(generateRandom32Bytes());
    }

    public static String start0x(String input){
        if(!input.startsWith("0x")){
            return "0x" + input;
        }
        return input;
    }

    public static String remove0x(String input){
        if(input.startsWith("0x")){
            return input.substring(2);
        }
        return input;
    }
}
