package org.nervos.muta.client.type.graphql_schema;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private String chainId;
    private List<String> confirmRoot;
    private List<String> cyclesUsed;
    private String height;
    private String execHeight;
    private String orderRoot;
    private String prevHash;
    private String proposer;
    private List<String> receiptRoot;
    private String stateRoot;
    private String timestamp;
    private String validatorVersion;
    private Proof proof;
    private List<Validator> validators;
}
