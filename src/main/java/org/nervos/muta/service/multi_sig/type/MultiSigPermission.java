package org.nervos.muta.service.multi_sig.type;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.primitive.U32;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiSigPermission {
  private Address owner;
  private List<Account> accounts;
  private U32 threshold;
  private String memo;
}
