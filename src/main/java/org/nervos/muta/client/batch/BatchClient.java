package org.nervos.muta.client.batch;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.MutaRequest;
import org.nervos.muta.client.type.graphql_schema.Block;
import org.nervos.muta.client.type.graphql_schema.Receipt;
import org.nervos.muta.client.type.graphql_schema.SignedTransaction;
import org.nervos.muta.client.type.request.GetBlockRequest;
import org.nervos.muta.client.type.request.GetReceiptRequest;
import org.nervos.muta.client.type.request.GetTransactionRequest;
import org.nervos.muta.exception.GraphQlError;

/**
 * A batch client for only GetBlock, GetReceipt and GetTransaction operations Note that GraphQl
 * doesn't support batch mode and doesn't support this kind of 'batch' operation
 *
 * @author Lycrus Hamster
 */
@Slf4j
public class BatchClient extends Client {

    public static String QUERY_NAME = "batch_query";

    public BatchClient(String url) {
        super(url);
    }

    public BatchClient(String url, OkHttpClient okHttpClient) {
        super(url, okHttpClient);
    }

    public static BatchClient defaultClient() {
        return new BatchClient("http://localhost:8000/graphql");
    }

    public List<BatchQueryResponse> query(List<BatchQuery> batchQueries) throws IOException {

        List<String> query_params = new ArrayList<>(batchQueries.size());
        List<String> query_bodies = new ArrayList<>(batchQueries.size());
        Map<String, Object> query_variables = new HashMap<>(batchQueries.size());
        Set<String> fragments = new HashSet<>();
        for (int index = 0; index < batchQueries.size(); index++) {
            BatchQuery item = batchQueries.get(index);
            String paramName = item.getBatchParamPrefix() + index;
            String paramNameWithType =
                    "$" + item.getBatchParamPrefix() + index + ":" + item.getBatchParamType();

            query_params.add(paramNameWithType);

            String queryBody = item.getBatchAliasPrefix() + index + item.getBatchQuery();
            queryBody = queryBody.replaceFirst("___VAR___", paramName);

            query_bodies.add(queryBody);

            query_variables.put(paramName, item.getParamValue());

            fragments.add(item.getBatchQueryFragment());
        }

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("query " + QUERY_NAME + "(");
        stringBuffer.append(String.join(", ", query_params));
        stringBuffer.append("){\n");
        stringBuffer.append(String.join("\n", query_bodies));
        stringBuffer.append("}\n");

        for (String frag : fragments) {
            stringBuffer.append(frag);
        }

        MutaRequest mutaRequest =
                new MutaRequest(QUERY_NAME, query_variables, stringBuffer.toString());

        String payload = this.getObjectMapper().writeValueAsString(mutaRequest);

        Response response = this.send(payload);

        List<BatchQueryResponse> ret = parse_batch(response, batchQueries);

        return ret;
    }

    public List<BatchQueryResponse> parse_batch(
            @NonNull Response response, List<BatchQuery> batchQueries) throws IOException {
        if (response.isSuccessful()) {

            if (response.body() == null) {
                throw new IOException("HTTP response.body() is null");
            }

            String responseBody = Objects.requireNonNull(response.body()).string();

            JsonNode root = this.getObjectMapper().readTree(responseBody);
            if (root.has("errors")) {
                throw new GraphQlError("batch query error,\n" + responseBody);

            } else {
                return do_parse(root.get("data"), batchQueries);
            }

        } else {
            throw new IOException("http error");
        }
    }

    public List<BatchQueryResponse> do_parse(JsonNode dataRoot, List<BatchQuery> batchQueries)
            throws IOException {
        List<BatchQueryResponse> ret = new ArrayList<>(batchQueries.size());

        for (int index = 0; index < batchQueries.size(); index++) {
            BatchQuery item = batchQueries.get(index);

            if (!dataRoot.has(item.getBatchAliasPrefix() + index)) {
                throw new GraphQlError(
                        "expecting alias not found: " + item.getBatchAliasPrefix() + index);
            }

            if (item instanceof GetBlockRequest) {
                ret.add(
                        this.getObjectMapper()
                                .treeToValue(
                                        dataRoot.get(item.getBatchAliasPrefix() + index),
                                        Block.class));
            } else if (item instanceof GetReceiptRequest) {
                ret.add(
                        this.getObjectMapper()
                                .treeToValue(
                                        dataRoot.get(item.getBatchAliasPrefix() + index),
                                        Receipt.class));
            } else if (item instanceof GetTransactionRequest) {
                ret.add(
                        this.getObjectMapper()
                                .treeToValue(
                                        dataRoot.get(item.getBatchAliasPrefix() + index),
                                        SignedTransaction.class));
            } else {
                throw new GraphQlError("unexpected item class: " + item.getClass().getName());
            }
        }

        return ret;
    }
}
