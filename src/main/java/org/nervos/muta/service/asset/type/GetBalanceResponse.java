package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;
import org.nervos.muta.client.type.primitive.U64;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBalanceResponse {
  private Hash asset_id;
  private Address user;
  private U64 balance;
}
