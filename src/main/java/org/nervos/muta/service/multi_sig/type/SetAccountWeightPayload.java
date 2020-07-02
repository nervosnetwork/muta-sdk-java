package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.primitive.U8;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetAccountWeightPayload {
  private Address multi_sig_address;
  private Address account_address;
  private U8 new_weight;
}
