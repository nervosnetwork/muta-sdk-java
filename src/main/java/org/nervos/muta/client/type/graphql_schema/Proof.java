package org.nervos.muta.client.type.graphql_schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Proof of a block. The proof is a aggregated signature of a block's hash. It contains bitmap to
 * indicate members who give their signature.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proof {
    /** work together with the list of authenticators, to indicate who give their signatures */
    public String bitmap;
    /** which block this proof belongs to */
    public String blockHash;
    /** which height of block this proof belongs to */
    public String height;
    /** which round of consensus this proof generate on */
    public String round;
    /** aggregate signature */
    public String signature;
}
