package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveAccountPayload {
  private Address multi_sig_address;
  private Address account_address;
}
