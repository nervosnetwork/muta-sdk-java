package org.nervos.muta.client.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;
import org.nervos.muta.client.type.graphql_schema_scalar.Uint64;

@AllArgsConstructor
@Data
public class MutaRequestOption {

  @NonNull private Hash chainId;

  @NonNull private Uint64 cyclesLimit;

  @NonNull private Uint64 cyclesPrice;

  @NonNull private Uint64 timeout;

  @NonNull private Address caller;

  private int polling_interval;

  private int polling_times;

  public static MutaRequestOption defaultMutaRequestOption() {
    return new MutaRequestOption(
        Hash.fromHexString("0xb6a4d7da21443f5e816e8700eea87610e6d769657d6b8ec73028457bf2ca4036"),
        Uint64.fromHexString("0x000000000000ffff"),
        Uint64.fromHexString("0x0000000000000000"),
        Uint64.fromHexString("0x0000000000000014"), // 20decimal
        Address.fromHexString("0x0000000000000000000000000000000000000000"),
        3000,
        10);
  }
}
