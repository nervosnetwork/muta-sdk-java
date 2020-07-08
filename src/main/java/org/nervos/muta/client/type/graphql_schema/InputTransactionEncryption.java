package org.nervos.muta.client.type.graphql_schema;

import java.util.List;
import lombok.Data;
import lombok.NonNull;
import org.web3j.rlp.*;

@Data
public class InputTransactionEncryption {
    @NonNull public GBytes pubkey;
    @NonNull public GBytes signature;
    @NonNull public GBytes txHash;

    public InputTransactionEncryption(GBytes pubkey, GBytes signature, GBytes txHash) {

        byte[] pub = RlpEncoder.encode(new RlpList(RlpString.create(pubkey.get())));

        this.pubkey = GBytes.fromByteArray(pub);
        byte[] sig = RlpEncoder.encode(new RlpList(RlpString.create(signature.get())));

        this.signature = GBytes.fromByteArray(sig);
        this.txHash = txHash;
    }

    public void appendSignatureAndPubkey(InputTransactionEncryption inputTransactionEncryption) {

        RlpList pubkeys = RlpDecoder.decode(this.pubkey.get());
        List<RlpType> pubkeylist = ((RlpList) pubkeys.getValues().get(0)).getValues();

        RlpList pubkeys_to_add = RlpDecoder.decode(inputTransactionEncryption.pubkey.get());
        List<RlpType> pubkeylist_to_add = ((RlpList) pubkeys_to_add.getValues().get(0)).getValues();

        pubkeylist.addAll(pubkeylist_to_add);

        this.pubkey = GBytes.fromByteArray(RlpEncoder.encode(new RlpList(pubkeylist)));

        // ================

        RlpList sigs = RlpDecoder.decode(this.signature.get());
        List<RlpType> siglist = ((RlpList) sigs.getValues().get(0)).getValues();

        RlpList sigs_to_add = RlpDecoder.decode(inputTransactionEncryption.signature.get());
        List<RlpType> siglist_to_add = ((RlpList) sigs_to_add.getValues().get(0)).getValues();

        siglist.addAll(siglist_to_add);

        this.signature = GBytes.fromByteArray(RlpEncoder.encode(new RlpList(siglist)));
    }
}