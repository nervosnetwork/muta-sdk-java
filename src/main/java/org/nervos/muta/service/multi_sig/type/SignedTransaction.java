package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
Note that: this SignedTransaction is not same as RawTransaction, do not use this class except in MultiSig query
This Class is supposed to be used internally, but you can also manually build this class to do some MultiSig
test before sending txs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedTransaction {
    private RawTransaction raw;
    private String tx_hash;
    private String pubkey;
    private String signature;

    public static class RawTransaction{
        private String chain_id;
        private long cycles_price;
        private long cycles_limit;
        private String nonce;
        private TransactionRequest request;
        private long timeout;
        private String sender;
    }

    public static class TransactionRequest{
        private String method;
        private String service_name;
        private String payload;//JSON string
    }
}
