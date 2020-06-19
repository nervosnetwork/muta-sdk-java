package org.nervos.muta.service.metadata.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    public String chain_id;
    public String common_ref;
    public String timeout_gap;
    public String cycles_limit;
    public String cycles_price;
    public String interval;
    public List<ValidatorExtend> verifier_list;
    public String propose_ratio;
    public String prevote_ratio;
    public String precommit_ratio;
    public String brake_ratio;
    public String tx_num_limit;
    public String max_tx_size;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidatorExtend {
        public String bls_pub_key;
        public String address;
        public String propose_weight;
        public String vote_weight;
    }
}

