package org.nervos.muta.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.nervos.muta.client.type.MutaRequest;
import org.nervos.muta.client.type.graphql_schema.*;
import org.nervos.muta.client.type.request.*;
import org.nervos.muta.exception.GraphQlError;

/**
 * Client plays role between remote GraphQl and local api. It handles all communications.
 *
 * @author Lycrus Hamster
 */
@Slf4j
@Getter
public class Client {

    /** default content-typt */
    private static final MediaType APPLICATION_JSON =
            MediaType.get("application/json; charset=utf-8");

    private static final String ZERO_UINT8 = "0x0000000000000000";

    /** remote GraphQl service url */
    private final String url;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Muta sdk uses OkHttpClient to talk HTTP protocol with GraphQl server :) */
    private final OkHttpClient httpClient;

    /**
     * Construct only by url, with Muta's default OkHttpClient. If you want to custom your OWN
     * OkHttpClient
     *
     * @param url {@link org.nervos.muta.client.Client#url}
     * @see org.nervos.muta.client.Client#Client(String, OkHttpClient)
     */
    public Client(String url) {
        this.url = url;
        this.httpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
    }

    /**
     * @param url {@link org.nervos.muta.client.Client#url}
     * @param okHttpClient {@link org.nervos.muta.client.Client#httpClient}
     */
    public Client(String url, OkHttpClient okHttpClient) {
        this.url = url;
        this.httpClient = okHttpClient;
    }

    /**
     * Create a Default Client to http://localhost:8000/graphql, That's the easiest way to talk to
     * default Muta-Chain for test
     *
     * @return A pre-defined Client
     */
    public static Client defaultClient() {
        return new Client("http://localhost:8000/graphql");
    }

    /**
     * Ask httpClient to send a http request containing GraphQl query/mutation payload
     *
     * @param payload HTTP request body
     * @return HTTP response
     * @throws IOException HTTP/network exceptions
     */
    protected Response send(String payload) throws IOException {
        RequestBody body = RequestBody.create(payload, APPLICATION_JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        return httpClient.newCall(request).execute();
    }

    /**
     * Parse a graphql query/mutation result into class with given type param T
     *
     * @param response OkHttp's HTTP respone
     * @param operation The name of GraphQl's operation, you could see GraphQl schema for more
     *     details
     * @param clazz To hold type param
     * @param <T> To which type you want to convert
     * @return Result of type T, note this may be null
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    protected <T> T parseGraphQlResponse(
            @NonNull Response response, String operation, Class<T> clazz) throws IOException {
        if (response.isSuccessful()) {

            if (response.body() == null) {
                throw new IOException("HTTP response.body() is null");
            }

            String responseBody = response.body().string();

            JsonNode root = objectMapper.readTree(responseBody);

            if (root.has("errors")) {

                log.debug(
                        "parseGraphQlResponse error: "
                                + objectMapper.writeValueAsString(responseBody));
                throw new GraphQlError(operation + " error,\n" + responseBody);

            } else if (root.get("data").has(operation)) {

                JsonNode result = root.get("data").get(operation);
                String data = result.toString();
                log.trace("parseGraphQlResponse data >>>" + data);

                T ret = objectMapper.treeToValue(result, clazz);
                return ret;
            }
            throw new GraphQlError(operation + " parse error");

        } else {

            throw new IOException("http error");
        }
    }

    /**
     * Start a GetBlock GraphQl query
     *
     * @param height The height want to query, <b>leave null for the latest</b>
     * @return The block info, note the block could be null
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public Block getBlock(GUint64 height) throws IOException {
        MutaRequest mutaRequest =
                new MutaRequest(
                        GetBlockRequest.operation,
                        new GetBlockRequest(height),
                        GetBlockRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Block ret = this.parseGraphQlResponse(response, GetBlockRequest.operation, Block.class);

        return ret;
    }

    /**
     * Start a GetTransaction GraphQl query
     *
     * @param txHash The transaction hash of transaction you want to query
     * @return The SignedTransaction when it sends, maybe null
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public SignedTransaction getTransaction(GHash txHash) throws IOException {
        MutaRequest mutaRequest =
                new MutaRequest(
                        GetTransactionRequest.operation,
                        new GetTransactionRequest(txHash),
                        GetTransactionRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        SignedTransaction ret =
                this.parseGraphQlResponse(
                        response, GetTransactionRequest.operation, SignedTransaction.class);
        return ret;
    }

    /**
     * Start a GetReceipt GraphQl query
     *
     * @param txHash The transaction hash of transaction you want to query
     * @return The Receipt of the transaction's execution result, maybe null
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public Receipt getReceipt(GHash txHash) throws IOException {
        MutaRequest mutaRequest =
                new MutaRequest(
                        GetReceiptRequest.operation,
                        new GetReceiptRequest(txHash),
                        GetReceiptRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Receipt ret =
                this.parseGraphQlResponse(response, GetReceiptRequest.operation, Receipt.class);
        return ret;
    }

    /**
     * Start a QueryService GraphQl query, which do read operations on services
     *
     * @param serviceName the name of the service
     * @param method the name of method under the service
     * @param payload the input data of the method
     * @param height on which height this queryService should run
     * @param caller give a caller of this queryService
     * @param cyclePrice a cyclePrice you want to use
     * @param cycleLimit a cyclePrice you want to use
     * @return The response of the execution of service
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public ServiceResponse queryService(
            @NonNull String serviceName,
            @NonNull String method,
            @NonNull String payload,
            GUint64 height,
            @NonNull GAddress caller,
            GUint64 cyclePrice,
            GUint64 cycleLimit)
            throws IOException {

        // graphql and json are happy with "null"
        // however muta's queryservice api use "" instead of "null"
        if (payload.equals("null")) {
            payload = "";
        }

        MutaRequest mutaRequest =
                new MutaRequest(
                        QueryServiceRequest.operation,
                        new QueryServiceRequest(
                                serviceName,
                                method,
                                payload,
                                height,
                                caller,
                                cyclePrice,
                                cycleLimit),
                        QueryServiceRequest.query);

        String _payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(_payload);

        ServiceResponse ret =
                this.parseGraphQlResponse(
                        response, QueryServiceRequest.operation, ServiceResponse.class);

        return ret;
    }

    /**
     * Start a SendTransaction GraphQl query, which do write operations on services
     *
     * @param inputRaw The information of transaction of SendTransaction
     * @param inputEncryption The signature of transaction of SendTransaction
     * @return The transaction hash
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public GHash sendTransaction(
            InputRawTransaction inputRaw, InputTransactionEncryption inputEncryption)
            throws IOException {
        MutaRequest mutaRequest =
                new MutaRequest(
                        SendTransactionRequest.operation,
                        new SendTransactionRequest(inputRaw, inputEncryption),
                        SendTransactionRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        log.trace("sendTransaction: " + payload);
        Response response = this.send(payload);

        GHash ret =
                this.parseGraphQlResponse(response, SendTransactionRequest.operation, GHash.class);
        return ret;
    }
}
