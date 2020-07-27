package org.nervos.muta.client.type.graphql_schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Validator {
    public GBytes pubkey;
    public int proposeWeight;
    public int voteWeight;
}
