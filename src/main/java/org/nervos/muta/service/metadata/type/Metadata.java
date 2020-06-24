package org.nervos.muta.service.metadata.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    private String chain_id;
    private String common_ref;
    private String timeout_gap;
    private String cycles_limit;
    private String cycles_price;
    private String interval;
    private List<ValidatorExtend> verifier_list;
    private String propose_ratio;
    private String prevote_ratio;
    private String precommit_ratio;
    private String brake_ratio;
    private String tx_num_limit;
    private String max_tx_size;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidatorExtend {
        private String bls_pub_key;
        private String address;
        private String propose_weight;
        private String vote_weight;
    }
}

