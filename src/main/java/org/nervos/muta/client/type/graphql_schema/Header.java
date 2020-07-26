package org.nervos.muta.client.type.graphql_schema;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private GHash chainId;
    private List<GHash> confirmRoot;
    private List<GUint64> cyclesUsed;
    private GUint64 height;
    private GUint64 execHeight;
    private GHash orderRoot;
    private GHash prevHash;
    private GAddress proposer;
    private List<GHash> receiptRoot;
    private GHash stateRoot;
    private GUint64 timestamp;
    private GUint64 validatorVersion;
    private Proof proof;
    private List<Validator> validators;
}
