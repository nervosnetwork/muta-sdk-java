package org.nervos.muta.client.type.graphql_schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proof {
    public String bitmap;
    public String blockHash;
    public String height;
    public String round;
    public String signature;
}
