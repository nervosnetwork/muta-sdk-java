package org.nervos.muta.client.type.request;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.util.Util;
import org.web3j.rlp.*;

import java.util.List;

@Data
public class TransactionEncryption {
    @NonNull
    public String pubkey;
    @NonNull
    public String signature;
    @NonNull
    public String txHash;

    public TransactionEncryption(String pubkey,String signature,String txHash){

        String pub = Util.start0x(Hex.toHexString(RlpEncoder.encode(new RlpList(
                RlpString.create(Hex.decode(Util.remove0x(pubkey)))
        ))));

        this.pubkey = pub;
        this.signature = Util.start0x(Hex.toHexString(RlpEncoder.encode(new RlpList(
                RlpString.create(Hex.decode(signature))
        ))));
        this.txHash = Util.start0x(txHash);
    }

    public void appendSignatureAndPubkey(TransactionEncryption transactionEncryption){

        RlpList pubkeys = RlpDecoder.decode( Hex.decode(Util.remove0x(this.pubkey)));
        List<RlpType> pubkeylist = ((RlpList)pubkeys.getValues().get(0)).getValues();

        RlpList pubkeys_to_add = RlpDecoder.decode( Hex.decode(Util.remove0x(transactionEncryption.pubkey)));
        List<RlpType> pubkeylist_to_add = ((RlpList)pubkeys_to_add.getValues().get(0)).getValues();

        pubkeylist.addAll(pubkeylist_to_add);

        this.pubkey = Util.start0x(Hex.toHexString(RlpEncoder.encode(new RlpList(pubkeylist))));

        //================

        RlpList sigs = RlpDecoder.decode( Hex.decode(Util.remove0x(this.signature)));
        List<RlpType> siglist = ((RlpList)sigs.getValues().get(0)).getValues();

        RlpList sigs_to_add = RlpDecoder.decode( Hex.decode(Util.remove0x(transactionEncryption.signature)));
        List<RlpType> siglist_to_add = ((RlpList)sigs_to_add.getValues().get(0)).getValues();

        siglist.addAll(siglist_to_add);

        this.signature = Util.start0x(Hex.toHexString(RlpEncoder.encode(new RlpList(siglist))));
    }
}
