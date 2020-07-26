package org.nervos.muta.util;

import java.math.BigInteger;
import java.util.Arrays;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

@Slf4j
public class CryptoUtil {
    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);
    static final BigInteger Q = SecP256K1Curve.q;
    static final ECDomainParameters CURVE =
            new ECDomainParameters(
                    CURVE_PARAMS.getCurve(),
                    CURVE_PARAMS.getG(),
                    CURVE_PARAMS.getN(),
                    CURVE_PARAMS.getH());
    static final BigInteger N = CURVE_PARAMS.getN();
    static final ECPoint G = CURVE_PARAMS.getG();

    public static ECPoint publicPoint(@NonNull BigInteger privateKey) {
        if (privateKey.bitLength() > N.bitLength()) {
            privateKey = privateKey.mod(N);
        }
        ECPoint ecPoint = new FixedPointCombMultiplier().multiply(G, privateKey);
        return ecPoint;
    }

    public static byte[] sign(@NonNull BigInteger privateKey, @NonNull byte[] data) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey, CryptoUtil.CURVE);
        signer.init(true, privKey);
        BigInteger[] components = signer.generateSignature(data);

        if (components[1].compareTo(HALF_CURVE_ORDER) >= 0) {
            components[1] = N.subtract(components[1]);
        }

        byte[] r = convertPositiveBigIntegerToBytes32(components[0]);
        byte[] s = convertPositiveBigIntegerToBytes32(components[1]);

        byte[] sig = new byte[64];
        System.arraycopy(r, 0, sig, 0, 32);
        System.arraycopy(s, 0, sig, 32, 32);

        return sig;
    }

    public static boolean verify(
            @NonNull byte[] signature, @NonNull byte[] data, @NonNull byte[] publicKey) {
        if (signature.length != 64) {
            throw new RuntimeException("verify, signature should be 64 bytes length");
        }

        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPublicKeyParameters params =
                new ECPublicKeyParameters(CURVE.getCurve().decodePoint(publicKey), CURVE);
        signer.init(false, params);

        byte[] r_b = Arrays.copyOfRange(signature, 0, 32);
        BigInteger r_x = convertBytes32ToPositiveBigInteger(r_b);
        byte[] s_b = Arrays.copyOfRange(signature, 32, 64);
        BigInteger s = convertBytes32ToPositiveBigInteger(s_b);
        try {
            return signer.verifySignature(data, r_x, s);
        } catch (NullPointerException e) {
            log.error("Caught NullPointerException inside bouncy castle", e);
            return false;
        }
    }

    public static boolean recovery(
            @NonNull byte[] signature, @NonNull byte[] data, @NonNull byte[] targetAddress) {
        if (signature.length != 64) {
            throw new RuntimeException("verify, signature should be 64 bytes length");
        }
        /*
        due to signature only contains r(R.x) and s, without v.
        we have to enumerate all recovery ids
         */
        byte[] r_b = Arrays.copyOfRange(signature, 0, 32);
        BigInteger r_x_origin = convertBytes32ToPositiveBigInteger(r_b);
        byte[] s_b = Arrays.copyOfRange(signature, 32, 64);
        BigInteger s_origin = convertBytes32ToPositiveBigInteger(s_b);

        for (int recId = 0; recId < 4; recId++) {
            BigInteger r_x = new BigInteger(r_x_origin.toByteArray());
            BigInteger s = new BigInteger(s_origin.toByteArray());
            // cause case of that r.x is done modulo n is really rare, so we calc these case later
            r_x = r_x.add(N.multiply(BigInteger.valueOf(recId / 2)));

            if (r_x.compareTo(Q) >= 0) {
                continue;
            }

            // regen r from r_x and the recId, cause same r_x could have 2 r_y
            X9IntegerConverter x9 = new X9IntegerConverter();
            byte[] compEnc = x9.integerToBytes(r_x, 1 + x9.getByteLength(CURVE.getCurve()));
            compEnc[0] = (byte) ((recId & 1) == 1 ? 0x03 : 0x02);
            // note tath ECCurve,decodePoint can handle different public key bytes format, e.g.
            // 0x02, 0x032 & 0x04;
            ECPoint r = CURVE.getCurve().decodePoint(compEnc);

            if (!r.multiply(N).isInfinity()) {
                continue;
            }
            // do the recovery calc
            // https://en.wikipedia.org/wiki/Elliptic_Curve_Digital_Signature_Algorithm#Public_key_recovery
            BigInteger e = new BigInteger(1, data);
            BigInteger eInv = BigInteger.ZERO.subtract(e).mod(N);
            BigInteger rInv = r_x.modInverse(N);
            BigInteger srInv = rInv.multiply(s).mod(N);
            BigInteger eInvrInv = rInv.multiply(eInv).mod(N);
            ECPoint q = ECAlgorithms.sumOfTwoMultiplies(G, eInvrInv, r, srInv);

            byte[] assumed_address = getAddressFromPublicKey(q);

            if (Arrays.equals(targetAddress, assumed_address)) {
                return true;
            }
        }
        return false;
    }

    public static byte[] getAddressFromPublicKey(@NonNull ECPoint point) {
        byte[] pub = point.getEncoded(false);
        pub = java.util.Arrays.copyOfRange(pub, 1, 65);

        byte[] addr = Util.keccak256(pub);

        return java.util.Arrays.copyOfRange(addr, 12, 32);
    }

    public static byte[] convertPositiveBigIntegerToBytes32(@NonNull BigInteger bi) {

        if (bi.signum() < 0) {
            throw new RuntimeException("convertPositiveBigIntegerToBytes32, input is negative");
        }

        byte[] input = bi.toByteArray();

        if (input.length == 0) {
            return new byte[32];
        }

        // remove all leading zeros
        int idx = 0;
        for (idx = 0; idx < input.length; idx++) {
            if (input[idx] != 0x00) {
                break;
            }
        }

        input = Arrays.copyOfRange(input, idx, input.length);

        if (input.length > 32) {
            throw new RuntimeException(
                    "convertBigIntegerToBytes32, length is to long: " + bi.toString(16));
        }

        byte[] ret = new byte[32];

        System.arraycopy(input, 0, ret, 32 - input.length, input.length);
        return ret;
    }

    public static BigInteger convertBytes32ToPositiveBigInteger(@NonNull byte[] input) {
        if (input.length != 32) {
            throw new RuntimeException("bytes32ToBigInteger, input's length is not 32");
        }

        byte[] dest = new byte[input.length + 1];
        System.arraycopy(input, 0, dest, 1, input.length);
        return new BigInteger(dest);
    }
}
