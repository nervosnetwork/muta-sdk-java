package org.nervos.muta.client.type.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.batch.BatchQuery;
import org.nervos.muta.client.type.graphql_schema.GHash;

/**
 * A class to indicate a GetReceipt <b>query</b> in GraphQl. In addition, it contains the param of
 * request
 *
 * @author Lycrus Hamster
 */
@Data
@AllArgsConstructor
public class GetReceiptRequest implements BatchQuery {
    public static String operation = "getReceipt";

    public static String query =
            "query getReceipt($txHash: Hash!) {\n"
                    + "  getReceipt(txHash: $txHash) {\n"
                    + "    txHash\n"
                    + "    height\n"
                    + "    cyclesUsed\n"
                    + "    events {\n"
                    + "      data\n"
                    + "      name\n"
                    + "      service\n"
                    + "    }\n"
                    + "    stateRoot\n"
                    + "    response {\n"
                    + "      serviceName\n"
                    + "      method\n"
                    + "      response {\n"
                    + "        code\n"
                    + "        errorMessage\n"
                    + "        succeedData\n"
                    + "      }\n"
                    + "    }\n"
                    + "  }\n"
                    + "}";

    public static String batch_query =
            " : getReceipt(txHash: $___VAR___) {\n" + " ...GetReceiptRequestFragment\n" + "  }\n";

    public static String batch_query_fragment =
            "    fragment GetReceiptRequestFragment on Receipt{\n"
                    + "    txHash\n"
                    + "    height\n"
                    + "    cyclesUsed\n"
                    + "    events {\n"
                    + "      data\n"
                    + "      name\n"
                    + "      service\n"
                    + "    }\n"
                    + "    stateRoot\n"
                    + "    response {\n"
                    + "      serviceName\n"
                    + "      method\n"
                    + "      response {\n"
                    + "        code\n"
                    + "        errorMessage\n"
                    + "        succeedData\n"
                    + "      }\n"
                    + "    }\n"
                    + "  }\n";
    public static String batch_param_prefix = "getReceipt_txHash_";
    public static String batch_param_type = "Hash!";
    public static String batch_alias_prefix = "getReceipt_";

    /** The height of querying block */
    private GHash txHash;

    @JsonIgnore
    @Override
    public String getBatchQuery() {
        return batch_query;
    }

    @JsonIgnore
    @Override
    public String getBatchParamPrefix() {
        return batch_param_prefix;
    }

    @JsonIgnore
    @Override
    public String getBatchParamType() {
        return batch_param_type;
    }

    @JsonIgnore
    @Override
    public String getBatchAliasPrefix() {
        return batch_alias_prefix;
    }

    @JsonIgnore
    @Override
    public String getBatchQueryFragment() {
        return batch_query_fragment;
    }

    @JsonIgnore
    @Override
    public Object getParamValue() {
        return this.getTxHash();
    }
}
