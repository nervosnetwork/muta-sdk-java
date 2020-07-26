package org.nervos.muta.client.type.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.batch.BatchQuery;
import org.nervos.muta.client.type.graphql_schema.GHash;

/**
 * A class to indicate a GetTransaction <b>query</b> in GraphQl. In addition, it contains the param
 * of request
 *
 * @author Lycrus Hamster
 */
@Data
@AllArgsConstructor
public class GetTransactionRequest implements BatchQuery {
    public static String operation = "getTransaction";

    public static String query =
            "query getTransaction($txHash: Hash!) {\n"
                    + "  getTransaction(txHash: $txHash) {\n"
                    + "    serviceName\n"
                    + "    method\n"
                    + "    payload\n"
                    + "    nonce\n"
                    + "    chainId\n"
                    + "    cyclesLimit\n"
                    + "    cyclesPrice\n"
                    + "    timeout\n"
                    + "    txHash\n"
                    + "    pubkey\n"
                    + "    sender\n"
                    + "  }\n"
                    + "}";

    public static String batch_query =
            " : getTransaction(txHash: $___VAR___) {\n"
                    + " ...GetTransactionRequestFragment\n"
                    + "  }\n";

    public static String batch_query_fragment =
            "    fragment GetTransactionRequestFragment on SignedTransaction{\n"
                    + "    serviceName\n"
                    + "    method\n"
                    + "    payload\n"
                    + "    nonce\n"
                    + "    chainId\n"
                    + "    cyclesLimit\n"
                    + "    cyclesPrice\n"
                    + "    timeout\n"
                    + "    txHash\n"
                    + "    pubkey\n"
                    + "    sender\n"
                    + "  }\n";

    public static String batch_param_prefix = "getTransaction_txHash_";
    public static String batch_param_type = "Hash!";
    public static String batch_alias_prefix = "getTransaction_";

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
