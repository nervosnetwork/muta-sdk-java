package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;

public class GetReceiptRequest {
    public static String operation = "getReceipt";

    public static String query = "query getReceipt($txHash: Hash!) {\n" +
            "  getReceipt(txHash: $txHash) {\n" +
            "    txHash\n" +
            "    height\n" +
            "    cyclesUsed\n" +
            "    events {\n" +
            "      data\n" +
            "      service\n" +
            "    }\n" +
            "    stateRoot\n" +
            "    response {\n" +
            "      serviceName\n" +
            "      method\n" +
            "      response {\n" +
            "        code\n" +
            "        errorMessage\n" +
            "        succeedData\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Data
    @AllArgsConstructor
    public static class Param{
        public String txHash;
    }
}
