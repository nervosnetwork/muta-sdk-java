package org.nervos.muta.client.type.primitive;

public class RawTransaction {
    private Hash chain_id;
    private U64 cycles_price;
    private U64 cycles_limit;
    private Hash nonce;
    private TransactionRequest request;
    private U64 timeout;
    private Address sender;
}
