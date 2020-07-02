package org.nervos.muta.client.type.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignedTransaction implements BatchQueryResponse {
  private String chainId;
  private String cyclesLimit;
  private String cyclesPrice;
  private String nonce;
  private String timeout;
  private String serviceName;
  private String method;
  private String payload;
  private String txHash;
  private String pubkey;
  private String signature;
  private String sender;
}
