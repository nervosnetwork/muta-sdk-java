package org.nervos.muta.client.type.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Vector;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block {

    public Header header;
    public Vector<String> orderedTxHashes;
    public String hash;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header{
        public String chainId;
        public Vector<String> confirmRoot;
        public Vector<String> cyclesUsed;
        public String height;
        public String execHeight;
        public String orderRoot;
        public String prevHash;
        public String proposer;
        public Vector<String> receiptRoot;
        public String stateRoot;
        public String timestamp;
        public String validatorVersion;
        public Proof proof;
        public Vector<Validator> validators;
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
