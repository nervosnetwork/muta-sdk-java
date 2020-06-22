package org.nervos.muta.wallet;

import lombok.Getter;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.util.CryptoUtil;
import org.nervos.muta.util.Util;

import java.math.BigInteger;

public class Account {

    public String getPrivateKey() {
        return Util.start0x(privateKey);
    }

    public String getPublicKey() {
        return Util.start0x(publicKey);
    }

    public String getAddress() {
        return Util.start0x(address);
    }

    public String privateKey;

    public String publicKey;

    public String address;

    private ECPoint point;


    public Account(String privateKey){
        this.privateKey = Util.remove0x(privateKey);

        this.point = CryptoUtil.publicPoint(new BigInteger(this.privateKey, 16));

        byte[] compressed = this.point.getEncoded(true);

        this.publicKey = Hex.toHexString(compressed);

        byte[] address = Util.keccak256(compressed);
        this.address = Hex.toHexString(address).substring(0,40);
    }

    public static Account generate(){
        return new Account(Util.remove0x(Util.generateRandom32BytesHex()));
    }

    public static Account defaultAccount(){
        return new Account("0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");
    }

    public byte[] sign(byte[] msgHash){
        byte[] ret = CryptoUtil.sign(new BigInteger(privateKey, 16),msgHash);
        return ret;
    }



}
