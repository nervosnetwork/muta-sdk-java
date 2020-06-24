package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.batch.BatchQuery;

@Data
@AllArgsConstructor
public class GetTransactionRequest implements BatchQuery {
    public static String operation = "getTransaction";

    public static String query =
            "query getTransaction($txHash: Hash!) {\n" +
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

    public static String batch_query =
                    " : getTransaction(txHash: $___VAR___) {\n" +
                    " ...GetTransactionRequestFragment\n"+
                    "  }\n";

    public static String batch_query_fragment =
                    "    fragment GetTransactionRequestFragment on SignedTransaction{\n" +
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
                    "  }\n";

    public static String batch_param_prefix = "getTransaction_txHash_";
    public static String batch_param_type = "Hash!";
    public static String batch_alias_prefix = "getTransaction_";

    private String txHash;

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
        return this.getTxHash();
    }

}
