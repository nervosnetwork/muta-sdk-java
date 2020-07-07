package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.batch.BatchQuery;
import org.nervos.muta.client.type.graphql_schema.GUint64;

@AllArgsConstructor
@Data
public class GetBlockRequest implements BatchQuery {

    public static String operation = "getBlock";

    public static String query =
            "query getBlock($height: Uint64) {\n"
                    + "  getBlock(height: $height) {\n"
                    + "    header {\n"
                    + "      chainId\n"
                    + "      confirmRoot\n"
                    + "      cyclesUsed\n"
                    + "      height\n"
                    + "      execHeight\n"
                    + "      orderRoot\n"
                    + "      prevHash\n"
                    + "      proposer\n"
                    + "      receiptRoot\n"
                    + "      stateRoot\n"
                    + "      timestamp\n"
                    + "      validatorVersion\n"
                    + "      proof {\n"
                    + "        bitmap\n"
                    + "        blockHash\n"
                    + "        height\n"
                    + "        round\n"
                    + "        signature\n"
                    + "      }\n"
                    + "      validators {\n"
                    + "        address\n"
                    + "        proposeWeight\n"
                    + "        voteWeight\n"
                    + "      }\n"
                    + "    }\n"
                    + "    orderedTxHashes\n"
                    + "    hash\n"
                    + "  }\n"
                    + "}";

    public static String batch_query =
            " : getBlock(height: $___VAR___) {\n" + "   ...GetBlockRequestFragment\n" + "  }\n";
    public static String batch_query_fragment =
            "    fragment GetBlockRequestFragment on Block{\n"
                    + "    header {\n"
                    + "      chainId\n"
                    + "      confirmRoot\n"
                    + "      cyclesUsed\n"
                    + "      height\n"
                    + "      execHeight\n"
                    + "      orderRoot\n"
                    + "      prevHash\n"
                    + "      proposer\n"
                    + "      receiptRoot\n"
                    + "      stateRoot\n"
                    + "      timestamp\n"
                    + "      validatorVersion\n"
                    + "      proof {\n"
                    + "        bitmap\n"
                    + "        blockHash\n"
                    + "        height\n"
                    + "        round\n"
                    + "        signature\n"
                    + "      }\n"
                    + "      validators {\n"
                    + "        address\n"
                    + "        proposeWeight\n"
                    + "        voteWeight\n"
                    + "      }\n"
                    + "    }\n"
                    + "    orderedTxHashes\n"
                    + "    hash\n"
                    + "  }\n";
    public static String batch_param_prefix = "getBlock_height_";
    public static String batch_param_type = "Uint64";
    public static String batch_alias_prefix = "getBlock_";

    private GUint64 height;

    @Override
    public String getBatchQuery() {
        return batch_query;
    }

    @Override
    public String getBatchParamPrefix() {
        return batch_param_prefix;
    }

    @Override
    public String getBatchParamType() {
        return batch_param_type;
    }

    @Override
    public String getBatchAliasPrefix() {
        return batch_alias_prefix;
    }

    @Override
    public String getBatchQueryFragment() {
        return batch_query_fragment;
    }

    @Override
    public Object getParamValue() {
        return this.getHeight();
    }
}
