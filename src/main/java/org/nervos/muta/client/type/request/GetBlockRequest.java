package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetBlockRequest {

    public static String operation = "getBlock";


    public static String query = "query getBlock($height: Uint64) {\n" +
            "  getBlock(height: $height) {\n" +
            "    header {\n" +
            "      chainId\n" +
            "      confirmRoot\n" +
            "      cyclesUsed\n" +
            "      height\n" +
            "      execHeight\n" +
            "      orderRoot\n" +
            "      prevHash\n" +
            "\n" +
            "      proposer\n" +
            "      receiptRoot\n" +
            "      stateRoot\n" +
            "      timestamp\n" +
            "      validatorVersion\n" +
            "      proof {\n" +
            "        bitmap\n" +
            "        blockHash\n" +
            "        height\n" +
            "        round\n" +
            "        signature\n" +
            "      }\n" +
            "      validators {\n" +
            "        address\n" +
            "        proposeWeight\n" +
            "        voteWeight\n" +
            "      }\n" +
            "    }\n" +
            "    orderedTxHashes\n" +
            "    hash\n" +
            "  }\n" +
            "}";


    @Data
    @AllArgsConstructor
    public static class Param{
        public String height;

    }
}
