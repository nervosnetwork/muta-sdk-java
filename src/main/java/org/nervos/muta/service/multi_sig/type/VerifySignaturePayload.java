package org.nervos.muta.service.multi_sig.type;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Bytes;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifySignaturePayload {
  public Hash tx_hash;
  private List<Bytes> pubkeys;
  private List<Bytes> signatures;
  private Address sender;
}
