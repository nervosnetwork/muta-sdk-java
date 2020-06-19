package org.nervos.muta.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import okhttp3.*;
import org.nervos.muta.client.type.MutaRequest;
import org.nervos.muta.client.type.request.*;
import org.nervos.muta.client.type.response.Block;
import org.nervos.muta.client.type.response.Receipt;
import org.nervos.muta.client.type.response.ServiceResponse;
import org.nervos.muta.client.type.response.Transaction;

import java.io.IOException;

public class Client {

    private static final MediaType APPLICATION_JSON = MediaType.get("application/json; charset=utf-8");

    private final String url;

    private final String ZERO_UINT8 = "0x0000000000000000";

    private final ObjectMapper objectMapper = new ObjectMapper();
    public OkHttpClient httpClient;

    public Client(String url) {
        this.url = url;
        this.httpClient = new OkHttpClient();
    }

    public static Client defaultClient(){
        return new Client("http://localhost:8000/graphql");
    }

    public Response send(String payload) throws IOException {
        RequestBody body = RequestBody.create(payload, APPLICATION_JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        return httpClient.newCall(request).execute();

    }

    public <T> T parse(Response response, String operation, Class<T> clazz) throws IOException {
        if (response.isSuccessful()) {
            String responseBody = response.body().string();

            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("errors")) {
                throw new IOException(operation + " error");

            } else if (root.get("data").has(operation)) {
                JsonNode result = root.get("data").get(operation);

                T ret = objectMapper.treeToValue(result, clazz);

                return ret;
            }
            throw new IOException(operation + "parse error");

        } else {
            throw new IOException("http error");
        }
    }

    public <T> T parseServiceResponse(ServiceResponse serviceResponse, Class<T> clazz) throws IOException{
        if (!ZERO_UINT8.equals(serviceResponse.code) ){
            throw new IOException("ServiceResponse code : " + serviceResponse.code+", message : " + serviceResponse.errorMessage);
        }

        String succeedMsg = serviceResponse.succeedData;

        T ret = objectMapper.readValue(succeedMsg,clazz);
        return ret;
    }

    public Block getBlock(String height) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(GetBlockRequest.operation, new GetBlockRequest.Param(height), GetBlockRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Block ret = this.parse(response, GetBlockRequest.operation, Block.class);

        return ret;
    }


    public Transaction getTransaction(String txHash) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(GetTransactionRequest.operation, new GetTransactionRequest.Param(txHash), GetTransactionRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Transaction ret = this.parse(response, GetTransactionRequest.operation, Transaction.class);
        return ret;
    }

    public Receipt getReceipt(String txHash) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(GetReceiptRequest.operation, new GetReceiptRequest.Param(txHash), GetReceiptRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        Receipt ret = this.parse(response, GetReceiptRequest.operation, Receipt.class);
        return ret;
    }

    public ServiceResponse queryServiceRaw(String serviceName, String method, String payload, String height, String caller, String cyclePrice, String cycleLimit) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(QueryServiceRequest.operation, new QueryServiceRequest.Param(
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

        ServiceResponse ret = this.parse(response, QueryServiceRequest.operation, ServiceResponse.class);

        return ret;
    }

    public <T> T queryService(@NonNull String serviceName, @NonNull String method, @NonNull String payload, String height, @NonNull String caller, String cyclePrice, String cycleLimit, Class<T> clazz) throws IOException {


        ServiceResponse serviceResponse = this.queryServiceRaw(serviceName, method, payload, height, caller, cyclePrice, cycleLimit);
        T ret = parseServiceResponse(serviceResponse, clazz);
        return ret;
    }



    public String sendTransaction(SendTransactionRequest.InputRawTransaction inputRaw, SendTransactionRequest.InputTransactionEncryption inputEncryption) throws IOException {
        MutaRequest mutaRequest = new MutaRequest(SendTransactionRequest.operation, new SendTransactionRequest.Param(inputRaw, inputEncryption), SendTransactionRequest.query);

        String payload = objectMapper.writeValueAsString(mutaRequest);
        Response response = this.send(payload);

        String ret = this.parse(response, SendTransactionRequest.operation, String.class);
        return ret;
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client("http://localhost:8000/graphql");

        Block block = client.getBlock("0x1");

    }
}
