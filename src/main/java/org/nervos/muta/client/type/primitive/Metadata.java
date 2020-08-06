package org.nervos.muta.client.type.primitive;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    private Hash chain_id;
    private String bech32_address_hrp;
    private Hex common_ref;
    private U64 timeout_gap;
    private U64 cycles_limit;
    private U64 cycles_price;
    private U64 interval;
    private List<ValidatorExtend> verifier_list;
    private U64 propose_ratio;
    private U64 prevote_ratio;
    private U64 precommit_ratio;
    private U64 brake_ratio;
    private U64 tx_num_limit;
    private U64 max_tx_size;
}
