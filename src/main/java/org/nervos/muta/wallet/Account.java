package org.nervos.muta.wallet;

import java.math.BigInteger;
import lombok.Getter;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.client.type.primitive.Address;
import org.nervos.muta.util.Bech32Util;
import org.nervos.muta.util.CryptoUtil;
import org.nervos.muta.util.Util;

/**
 * Account is a class to hold all private key, public key and address info, and is on duty of sign
 * tx
 *
 * @author Lycrus Hamster
 */
@Getter
public class Account {

    // 32 bytes
    private final byte[] privateKey;
    // compressed+32bytes=33bytes
    private final byte[] publicKey;
    // 20bytes,keccak(hash)[0..20]
    private final byte[] address;

    private final String bech32Address;

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

        this.address = CryptoUtil.getAddressFromPublicKey(this.point);

        this.bech32Address = Bech32Util.encodeAddress(address);
    }

    /**
     * Create account by private key in hex string format
     *
     * @param hexString private key in hex string
     * @return created account
     */
    public static Account fromHexString(String hexString) {
        Util.isValidHex(hexString);
        return new Account(Hex.decode(Util.remove0x(hexString)));
    }

    /**
     * Create account by private key in byte[] format
     *
     * @param b private key in byte[] format
     * @return created account
     */
    public static Account fromByteArray(byte[] b) {
        return new Account(b);
    }

    /**
     * Create account by a random private key
     *
     * @return created account
     */
    public static Account generate() {
        return Account.fromHexString(Util.generateRandom32BytesHex());
    }

    /**
     * Return account with default private key, this is easy in dev and test env
     *
     * @return created account
     */
    public static Account defaultAccount() {
        return Account.fromHexString(
                "0xe05d9e08a18cf5573a92d030342c3b45395cd952e02346ba78e16421ee9dad88");
    }

    /**
     * Get inner address in bech32 format and return in byte array
     *
     * @param bech32Address address in bech32 format
     * @return inner address in byte array
     */
    public static byte[] convertBech32AddressToBytesAddress(String bech32Address) {
        return Bech32Util.decodeAddress(bech32Address);
    }
    /**
     * Get inner address in bech32 format and return in hex string
     *
     * @param bech32Address address in bech32 format
     * @return inner address in hex string
     */
    public static String convertBech32AddressToHexAddress(String bech32Address) {
        return Util.start0x(Hex.toHexString(Bech32Util.decodeAddress(bech32Address)));
    }

    /**
     * Get bech32 format address from inner address in byte array
     *
     * @param address address in bech32 format
     * @return bech32 address
     */
    public static String convertBytesAddressToBech32Address(byte[] address) {
        return Bech32Util.encodeAddress(address);
    }

    /**
     * Get bech32 format address from inner address in hex string
     *
     * @param address address in bech32 format
     * @return bech32 address
     */
    public static String convertHexAddressToBech32Address(String address) {
        return Bech32Util.encodeAddress(Hex.decode(Util.remove0x(address)));
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

    public GAddress getGAddress() {
        return GAddress.fromByteArray(address);
    }

    public Address getAddress() {
        return Address.fromByteArray(address);
    }

    public String getAddressHex() {
        return Util.start0x(Hex.toHexString(address));
    }

    /**
     * Sign a data by this account's private key, the data should be a digest of certain message
     *
     * @param msgHash digest of message
     * @return signed signature
     */
    public byte[] sign(byte[] msgHash) {
        byte[] ret =
                CryptoUtil.sign(
                        new BigInteger(Arrays.concatenate(new byte[] {0x00}, privateKey)), msgHash);
        return ret;
    }
}
