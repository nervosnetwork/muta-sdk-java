package org.nervos.muta.client.type.primitive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawTransaction {
    private Hash chain_id;
    private U64 cycles_price;
    private U64 cycles_limit;
    private Hash nonce;
    private TransactionRequest request;
    private U64 timeout;
    private Address sender;
}
