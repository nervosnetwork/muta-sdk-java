package org.nervos.muta.client.type.request;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;

@Data
public class TransactionEncryption {
    @NonNull
    public String pubkey;
    @NonNull
    public String signature;
    @NonNull
    public String txHash;

    public TransactionEncryption(String txHash, String pubkey,String signature){
        this.pubkey = Hex.toHexString(RlpEncoder.encode(new RlpList(
                RlpString.create(pubkey)
        )));
        this.signature = Hex.toHexString(RlpEncoder.encode(new RlpList(
                RlpString.create(signature)
        )));
        this.txHash = txHash;
    }

    public void appendSignatureAndPubkey(TransactionEncryption transactionEncryption){

        RlpList pubkeys = RlpDecoder.decode( Hex.decode(this.pubkey));
        pubkeys.getValues().add(RlpString.create(transactionEncryption.pubkey));

        this.pubkey = Hex.toHexString(RlpEncoder.encode(pubkeys));

        RlpList signatures = RlpDecoder.decode( Hex.decode(this.signature));
        signatures.getValues().add(RlpString.create(transactionEncryption.signature));

        this.signature = Hex.toHexString(RlpEncoder.encode(signatures));
    }
}
