package org.nervos.muta.client.type.graphql_schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignedTransaction implements BatchQueryResponse {
    private GHash chainId;
    private GUint64 cyclesLimit;
    private GUint64 cyclesPrice;
    private GHash nonce;
    private GUint64 timeout;
    private GAddress sender;
    private String serviceName;
    private String method;
    private String payload;
    private GHash txHash;
    private GBytes pubkey;
    private GBytes signature;
}
