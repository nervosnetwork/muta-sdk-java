package org.nervos.muta.client.type.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    public String chainId;
    public String cyclesLimit;
    public String cyclesPrice;
    public String nonce;
    public String timeout;
    public String serviceName;
    public String method;
    public String payload;
    public String txHash;
    public String pubkey;
    public String signature;
    public String sender;
}
