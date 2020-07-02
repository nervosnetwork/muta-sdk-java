package org.nervos.muta.client.type.primitive;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.graphql_schema_scalar.Bytes;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;
import org.nervos.muta.client.type.graphql_schema_scalar.Uint64;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
  private Hash chain_id;
  private Bytes common_ref;
  private Uint64 timeout_gap;
  private Uint64 cycles_limit;
  private Uint64 cycles_price;
  private Uint64 interval;
  private List<ValidatorExtend> verifier_list;
  private Uint64 propose_ratio;
  private Uint64 prevote_ratio;
  private Uint64 precommit_ratio;
  private Uint64 brake_ratio;
  private Uint64 tx_num_limit;
  private Uint64 max_tx_size;
}
