package org.nervos.muta.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.nervos.muta.client.type.MutaRequest;
import org.nervos.muta.client.type.request.*;
import org.nervos.muta.client.type.response.Block;
import org.nervos.muta.client.type.response.Receipt;
import org.nervos.muta.client.type.response.ServiceResponse;
import org.nervos.muta.client.type.response.Transaction;
import org.nervos.muta.exception.GraphQlError;
import org.nervos.muta.exception.ServiceResponseError;
import org.nervos.muta.util.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class Client {

    private static final MediaType APPLICATION_JSON = MediaType.get("application/json; charset=utf-8");

    private final String url;

    private final String ZERO_UINT8 = "0x0000000000000000";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient httpClient;

    public Client(String url) {
        this.url = url;
        this.httpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public Client(String url, OkHttpClient okHttpClient){
        this.url = url;
        this.httpClient = okHttpClient;
    }

    public static Client defaultClient(){
        return new Client("http://localhost:8000/graphql");
    }

    protected Response send(String payload) throws IOException {
        RequestBody body = RequestBody.create(payload, APPLICATION_JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        return httpClient.newCall(request).execute();
    }

    // parse a graphql result into class
    protected  <T> T parseGraphQlResponse(@NonNull Response response, String operation, Class<T> clazz) throws IOException {
        if (response.isSuccessful()) {

            if(response.body() == null){
                throw new GraphQlError("response.body() is null");
            }

            String responseBody = response.body().string();

            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("errors")) {
               log.debug("parseGraphQlResponse error: "+ objectMapper.writeValueAsString(responseBody));
                throw new GraphQlError(operation + " error,\n" + responseBody);
            } else if (root.get("data").has(operation)) {
                JsonNode result = root.get("data").get(operation);

                String data = result.toString();
                log.debug("parseGraphQlResponse data >>>"+data);
                T ret = objectMapper.treeToValue(result, clazz);

                return ret;
            }
            throw new GraphQlError(operation + " parse error");

        } else {

            throw new IOException("http error");
        }
    }

    public Block getBlock(String height) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(GetBlockRequest.operation, new GetBlockRequest(height), GetBlockRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Block ret = this.parseGraphQlResponse(response, GetBlockRequest.operation, Block.class);

        return ret;
    }



    public Transaction getTransaction(String txHash) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(GetTransactionRequest.operation, new GetTransactionRequest(txHash), GetTransactionRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Transaction ret = this.parseGraphQlResponse(response, GetTransactionRequest.operation, Transaction.class);
        return ret;
    }

    public Receipt getReceipt(String txHash) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(GetReceiptRequest.operation, new GetReceiptRequest(txHash), GetReceiptRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Receipt ret = this.parseGraphQlResponse(response, GetReceiptRequest.operation, Receipt.class);
        return ret;
    }

    public ServiceResponse queryService(@NonNull String serviceName, @NonNull String method, String payload, String height, @NonNull String caller, String cyclePrice, String cycleLimit) throws IOException {

        //graphql and json are happy with null
        //however muta's queryservice api need String, not Option<String>,
        //thus we must pass something like an empty string
        if (payload == null){
            payload = "";
        }

        MutaRequest mutaRequest = new MutaRequest(QueryServiceRequest.operation, new QueryServiceRequest(
                serviceName,
                method,
                payload,
                height,
                caller,
                cyclePrice,
                cycleLimit
        ), QueryServiceRequest.query);

        String _payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(_payload);

        ServiceResponse ret = this.parseGraphQlResponse(response, QueryServiceRequest.operation, ServiceResponse.class);

        return ret;
    }

    public String sendTransaction(RawTransaction inputRaw, TransactionEncryption inputEncryption) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(SendTransactionRequest.operation, new SendTransactionRequest(inputRaw, inputEncryption), SendTransactionRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        log.debug("sendTransaction: "+payload);
        Response response = this.send(payload);

        String ret = this.parseGraphQlResponse(response, SendTransactionRequest.operation, String.class);
        return ret;
    }
}
