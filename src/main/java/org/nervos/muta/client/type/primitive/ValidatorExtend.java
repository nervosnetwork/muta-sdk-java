package org.nervos.muta.client.type.primitive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Bytes;
import org.nervos.muta.client.type.graphql_schema_scalar.Uint64;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorExtend {
  private Bytes bls_pub_key;
  private Address address;
  private Uint64 propose_weight;
  private Uint64 vote_weight;
}
