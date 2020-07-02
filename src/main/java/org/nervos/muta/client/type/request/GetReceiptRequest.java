package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.batch.BatchQuery;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;

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

  private Hash txHash;

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
