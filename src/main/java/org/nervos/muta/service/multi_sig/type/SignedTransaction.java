package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
Note that: this SignedTransaction is not same as RawTransaction, do not use this class except in MultiSig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedTransaction {
    public RawTransaction raw;
    public String tx_hash;
    public String pubkey;
    public String signature;

    public static class RawTransaction{
        public String chain_id;
        public long cycles_price;
        public long cycles_limit;
        public String nonce;
        public TransactionRequest request;
        public long timeout;
        public String sender;
    }

    public static class TransactionRequest{
        public String method;
        public String service_name;
        public String payload;//JSON string
    }
}
