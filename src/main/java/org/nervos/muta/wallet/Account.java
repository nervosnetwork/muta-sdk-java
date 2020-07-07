package org.nervos.muta.wallet;

import java.math.BigInteger;
import lombok.Getter;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.util.CryptoUtil;
import org.nervos.muta.util.Util;

@Getter
public class Account {

    // 32 bytes
    private final byte[] privateKey;
    // compressed+32bytes=33bytes
    private final byte[] publicKey;
    // 20bytes,keccak(hash)[0..20]
    private final byte[] address;

    private final ECPoint point;

    private Account(byte[] privateKey) {
        if (privateKey.length != 32) {
            throw new RuntimeException("privateKey should be 32 bytes");
        }

        this.privateKey = privateKey;

        this.point =
                CryptoUtil.publicPoint(
                        new BigInteger(Arrays.concatenate(new byte[] {0x00}, privateKey)));
        byte[] compressed = this.point.getEncoded(true);

        if (compressed.length != 33) {
            throw new RuntimeException("publicKey should be 32 bytes, that's weird");
        }
        this.publicKey = compressed;
        byte[] addr = Util.keccak256(compressed);
        this.address = java.util.Arrays.copyOf(addr, 20);
    }

    public static Account fromHexString(String hexString) {
        Util.isValidHex(hexString);
        return new Account(Hex.decode(Util.remove0x(hexString)));
    }

    public static Account fromByteArray(byte[] b) {
        return new Account(b);
    }

    public static Account generate() {
        return Account.fromHexString(Util.generateRandom32BytesHex());
    }

    public static Account defaultAccount() {
        return Account.fromHexString(
                "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");
    }

    public byte[] getPrivateKeyByteArray() {
        return privateKey;
    }

    public String getPrivateKeyHex() {
        return Util.start0x(Hex.toHexString(privateKey));
    }

    public BigInteger getPrivateKeyBigInteger() {
        return new BigInteger(Arrays.concatenate(new byte[] {0x00}, privateKey));
    }

    public byte[] getPublicKeyByteArray() {
        return publicKey;
    }

    public String getPublicKeyHex() {
        return Util.start0x(Hex.toHexString(publicKey));
    }

    public byte[] getAddressByteArray() {
        return address;
    }

    public GAddress getAddress() {
        return GAddress.fromByteArray(address);
    }

    public String getAddressHex() {
        return Util.start0x(Hex.toHexString(address));
    }

    public byte[] sign(byte[] msgHash) {
        byte[] ret =
                CryptoUtil.sign(
                        new BigInteger(Arrays.concatenate(new byte[] {0x00}, privateKey)), msgHash);
        return ret;
    }
}
