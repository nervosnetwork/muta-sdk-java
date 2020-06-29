package org.nervos.muta.client.type.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block implements BatchQueryResponse {

    private Header header;
    private List<String> orderedTxHashes;
    private String hash;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header{
        private String chainId;
        private List<String> confirmRoot;
        private List<String> cyclesUsed;
        private String height;
        private String execHeight;
        private String orderRoot;
        private String prevHash;
        private String proposer;
        private List<String> receiptRoot;
        private String stateRoot;
        private String timestamp;
        private String validatorVersion;
        private Proof proof;
        private List<Validator> validators;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Proof{
        public String bitmap;
        public String blockHash;
        public String height;
        public String round;
        public String signature;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Validator{
        public String address;
        public String proposeWeight;
        public String voteWeight;
    }


}
