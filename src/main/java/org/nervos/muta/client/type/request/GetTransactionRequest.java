package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;

public class GetTransactionRequest {
    public static String operation = "getTransaction";

    public static String query = "query getTransaction($txHash: Hash!) {\n" +
            "  getTransaction(txHash: $txHash) {\n" +
            "    serviceName\n" +
            "    method\n" +
            "    payload\n" +
            "    nonce\n" +
            "    chainId\n" +
            "    cyclesLimit\n" +
            "    cyclesPrice\n" +
            "    timeout\n" +
            "    txHash\n" +
            "    pubkey\n" +
            "    sender\n" +
            "  }\n" +
            "}";

    @Data
    @AllArgsConstructor
    public static class Param{
        public String txHash;
    }
}
