package org.nervos.muta.client.type.primitive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
Note that this SignedTransaction is not same as response.SignedTransaction. This SignedTransaction is used
inside Muta. Here we expose this class because you can use it in Multi_Sig service to expand your test
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedTransaction {
  private RawTransaction raw;
  private String tx_hash;
  private String pubkey;
  private String signature;
}
