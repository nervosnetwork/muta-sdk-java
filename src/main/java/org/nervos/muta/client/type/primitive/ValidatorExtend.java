package org.nervos.muta.client.type.primitive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorExtend {
    private Hex bls_pub_key;
    private Address address;
    private U32 propose_weight;
    private U32 vote_weight;
}
