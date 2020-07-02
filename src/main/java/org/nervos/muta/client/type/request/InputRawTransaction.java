package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;
import org.nervos.muta.client.type.graphql_schema_scalar.Uint64;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;

@Data
@AllArgsConstructor
@Builder
public class InputRawTransaction {

  @NonNull private Hash chainId;
  @NonNull private Uint64 cyclesLimit;
  @NonNull private Uint64 cyclesPrice;
  @NonNull private Hash nonce;
  @NonNull private String method;
  @NonNull private String payload;
  @NonNull private String serviceName;
  @NonNull private Uint64 timeout;
  @NonNull private Address sender;

  public byte[] encode() {
    return RlpEncoder.encode(
        new RlpList(
            RlpString.create(this.chainId.get()),
            RlpString.create(this.cyclesLimit.get()),
            RlpString.create(this.cyclesPrice.get()),
            RlpString.create(this.nonce.get()),
            RlpString.create(this.method),
            RlpString.create(this.serviceName),
            RlpString.create(this.payload),
            RlpString.create(this.timeout.get()),
            new RlpList(RlpString.create(this.sender.get()))));
  }
}
