package org.nervos.muta;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.MutaRequestOption;
import org.nervos.muta.client.type.request.RawTransaction;
import org.nervos.muta.client.type.request.TransactionEncryption;
import org.nervos.muta.client.type.response.Receipt;
import org.nervos.muta.exception.ReceiptResponseError;
import org.nervos.muta.util.Util;
import org.nervos.muta.wallet.Account;

import java.io.IOException;
import java.math.BigInteger;

@Getter
@Slf4j
public class Muta {
    private final Client client;
    private final Account account;
    private final MutaRequestOption mutaRequestOption;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Muta(Client client, Account account, MutaRequestOption defaultReqOption) {


        if (account == null) {
            account = Account.defaultAccount();
            log.warn("you are using default account: "+account.getAddress());
        }

        if (defaultReqOption == null) {
            defaultReqOption = MutaRequestOption.defaultMutaRequestOption();
        }

        this.mutaRequestOption = defaultReqOption;
        this.account = account;
        this.client = client;
    }


    public static Muta defaultMuta() {
        return new Muta(Client.defaultClient(), null, null);
    }

    public <T> T queryService(@NonNull String serviceName, @NonNull String method, String payload, String height, String caller, String cyclePrice, String cycleLimit, Class<T> clazz) throws IOException {
        if (payload == null) {
            payload = "";
        }
        if (caller == null) {
            caller = mutaRequestOption.getCaller();
        }

        checkClient();

        T ret = this.client.queryService(serviceName, method, payload, height, caller, cyclePrice, cycleLimit, clazz);
        return ret;
    }

    //queryService without payload
    public <T> T queryService(@NonNull String serviceName, @NonNull String method, Class<T> clazz) throws IOException {

        T ret = this.queryService(serviceName, method, null, null, null, null, null, clazz);
        return ret;
    }

    //queryService with given payload
    public <T, P> T queryService(@NonNull String serviceName, @NonNull String method, @NonNull P payloadData, Class<T> clazz) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);
        T ret = this.queryService(serviceName, method, payload, null, null, null, null, clazz);
        return ret;
    }

    // sign and send transaction
    public String sendTransaction(String chainId,
                                  String cyclesLimit,
                                  String cyclesPrice,
                                  String nonce,
                                  String timeout,
                                  @NonNull String serviceName,
                                  @NonNull String method,
                                  String payload) throws IOException {

        RawTransaction rawTransaction = this.compose(
                chainId,
                cyclesLimit,
                cyclesPrice,
                nonce,
                timeout,
                serviceName,
                method,
                payload,
                null
        );

        byte[] encoded = rawTransaction.encode();

        byte[] txHash = Util.keccak256(encoded);

        TransactionEncryption transactionEncryption = this.signTransaction(rawTransaction);

        checkClient();

        String ret = this.sendTransaction(rawTransaction, transactionEncryption);
        return ret;
    }

    //this is the commonly used sendTransaction
    //sendTransaction only with serviceName, method and payloadData
    public <P> String sendTransaction(@NonNull String serviceName,
                                      @NonNull String method,
                                      P payloadData) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);

        return this.sendTransaction(null,
                null,
                null,
                null,
                null,
                serviceName,
                method,
                payload
        );
    }

    public String sendTransaction(RawTransaction rawTransaction,
                                  TransactionEncryption transactionEncryption) throws IOException {
        checkClient();
        String ret = this.client.sendTransaction(rawTransaction, transactionEncryption);
        return ret;
    }

    public <P, R> R sendTransactionAndPollResult(@NonNull String serviceName,
                                                 @NonNull String method,
                                                 P payloadData,
                                                 Class<R> clazz) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);

        String txHash = this.sendTransaction(null,
                null,
                null,
                null,
                null,
                serviceName,
                method,
                payload
        );

        return getReceiptSucceedDataRetry(txHash, clazz);
    }

    // while receipt is ready but the service response is error, thrown runtime exception
    public <R> R getReceiptSucceedData(String txHash, Class<R> clazz) throws IOException {
        checkClient();

        Receipt receipt = client.getReceipt(txHash);

        if(!BigInteger.ZERO.equals(new BigInteger(Util.remove0x(receipt.getResponse().getResponse().getCode()),16))){
            throw new ReceiptResponseError(receipt.getResponse().getServiceName(),receipt.getResponse().getMethod(),receipt.getResponse().getResponse().getCode(),receipt.getResponse().getResponse().getErrorMessage());
        }

        if(Util.MutaVoid.class.equals(clazz)){
            return (R)new Util.MutaVoid();
        }

        R ret = objectMapper.readValue(receipt.getResponse().getResponse().getSucceedData(), clazz);

        return ret;
    }

    public <P> P getReceiptSucceedDataRetry(String txHash, Class<P> clazz) throws IOException {
        int times = this.mutaRequestOption.getPolling_times();
        while (times > 0) {

            try {

                P ret = getReceiptSucceedData(txHash, clazz);
                return ret;
            } catch (Exception e) {
                //e.printStackTrace();
            }

            try {
                Thread.sleep(this.mutaRequestOption.getPolling_interval());
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }

            times--;
        }

        throw new IOException("getReceipt() fails to retrieve tx data");
    }

    public <P> RawTransaction compose(@NonNull String serviceName, @NonNull String method, P payloadData,String sender) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);
        return compose(null,
                null,
                null,
                null,
                null,
                serviceName,
                method,
                payload,
                sender
        );
    }

    public RawTransaction compose(String chainId,
                                  String cyclesLimit,
                                  String cyclesPrice,
                                  String nonce,
                                  String timeout,
                                  @NonNull String serviceName,
                                  @NonNull String method,
                                  String payload,
                                  String sender) throws IOException {

        if (chainId == null) {
            chainId = mutaRequestOption.getChainId();
        }
        if (cyclesLimit == null) {
            cyclesLimit = mutaRequestOption.getCyclesLimit();
        }
        if (cyclesPrice == null) {
            cyclesPrice = mutaRequestOption.getCyclesPrice();
        }
        if (nonce == null) {
            nonce = Util.generateRandom32BytesHex();
        }

        if (timeout == null) {
            checkClient();
            BigInteger latestHeight = this.client.getLatestHeight();
            timeout = Util.start0x(new BigInteger(Util.remove0x(mutaRequestOption.getTimeout()), 16).add(latestHeight).toString(16));
        }
        if (payload == null) {
            payload = "";
        }

        if(sender == null){
            sender = account.getAddress();
        }

        RawTransaction rawTransaction = new RawTransaction(
                chainId,
                cyclesLimit,
                cyclesPrice,
                method,
                nonce,
                payload,
                serviceName,
                timeout,
                sender
        );

        return rawTransaction;
    }

    // sign a raw transaction, do nothing more
    public TransactionEncryption signTransaction(RawTransaction rawTransaction) {
        byte[] txHash = Util.keccak256(rawTransaction.encode());

        byte[] sig = this.account.sign(txHash);

        TransactionEncryption transactionEncryption = new TransactionEncryption(
                account.getPublicKey(),
                Hex.toHexString(sig),
                Hex.toHexString(txHash)
        );

        return transactionEncryption;
    }

    // sign a raw transaction and append your sig to given transaction sig (transactionEncryption)
    public TransactionEncryption appendSignedTransaction(RawTransaction rawTransaction, TransactionEncryption transactionEncryption) throws IOException {
        byte[] txHash = Util.keccak256(rawTransaction.encode());
        if (!Util.start0x(Hex.toHexString(txHash)).equals(transactionEncryption.txHash)) {
            throw new IOException("RawTransaction's txHash and TransactionEncryption's doesn't match");
        }

        TransactionEncryption signed = this.signTransaction(rawTransaction);

        transactionEncryption.appendSignatureAndPubkey(signed);

        return transactionEncryption;
    }

    private void checkClient() throws RuntimeException {
        if (this.client == null) {
            throw new RuntimeException("the client of Muta instance hasn't been set");
        }
    }
}
