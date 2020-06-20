package org.nervos.muta.util;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

public class CryptoUtil {
    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    public static final ECDomainParameters CURVE =
            new ECDomainParameters(
                    CURVE_PARAMS.getCurve(),
                    CURVE_PARAMS.getG(),
                    CURVE_PARAMS.getN(),
                    CURVE_PARAMS.getH());
    static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);

    public static ECPoint publicPoint(BigInteger privateKey) {
        if (privateKey.bitLength() > CURVE.getN().bitLength()) {
            privateKey = privateKey.mod(CURVE.getN());
        }
        ECPoint ecPoint = new FixedPointCombMultiplier().multiply(CURVE.getG(), privateKey);
        return ecPoint;
    }

    public static byte[] sign(BigInteger privateKey, byte[] data){
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey, CryptoUtil.CURVE);
        signer.init(true, privKey);
        BigInteger[] components = signer.generateSignature(data);

        if (components[1].compareTo(HALF_CURVE_ORDER)<=0){
            components[1] = CURVE.getN().subtract(components[1]);
        }

        byte[] r = convertBigIntegerToBytes32(components[0]);
        byte[] s = convertBigIntegerToBytes32(components[1]);

        byte[] sig = new byte[64];
        System.arraycopy(r,0,sig,0,32);
        System.arraycopy(s,0,sig,32,32);

        return sig;
    }

    public static void main(String[] args) {
        BigInteger priv = new BigInteger("45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f",16);
        byte[] data = Hex.decode("1234567890123456789012345678901234567890123456789012345678901234");

        System.out.println(Hex.toHexString(CryptoUtil.sign(priv, data)));
    }

    public static byte[] convertBigIntegerToBytes32(BigInteger bi){
        byte[] ret = new byte[32];
        byte[] input = bi.toByteArray();

        if (input[0] == 0x00){
            input = Arrays.copyOfRange(input,1,input.length);
        }

        if (input.length>32){
            throw new RuntimeException("convertBigIntegerToBytes32, length is to long: " + bi.toString(16) );
        }

        System.arraycopy(input, 0, ret, 32-input.length, 32);
        return ret;
    }

}
