package org.nervos.muta;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.MutaRequestOption;
import org.nervos.muta.client.type.request.RawTransaction;
import org.nervos.muta.client.type.request.TransactionEncryption;
import org.nervos.muta.client.type.response.Receipt;
import org.nervos.muta.util.Util;
import org.nervos.muta.wallet.Account;

import java.io.IOException;
import java.math.BigInteger;

public class Muta {
    public Client client;
    public Account account;
    public MutaRequestOption mutaRequestOption;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Muta(Client client, Account account, MutaRequestOption defaultReqOption) {
        if (client == null) {
            client = Client.defaultClient();
        }

        if (account == null) {
            account = Account.defaultAccoutn();
        }

        if (defaultReqOption == null) {
            defaultReqOption = MutaRequestOption.defaultMutaRequestOption();
        }

        this.mutaRequestOption = defaultReqOption;
        this.account = account;
        this.client = client;
    }


    public static Muta defaultMuta() {
        return new Muta(null, null, null);
    }

    public <T> T queryService(@NonNull String serviceName, @NonNull String method, String payload, String height, String caller, String cyclePrice, String cycleLimit, Class<T> clazz) throws IOException {
        if (payload == null) {
            payload = "";
        }
        if (caller == null) {
            caller = mutaRequestOption.caller;
        }


        T ret = this.client.queryService(serviceName, method, payload, height, caller, cyclePrice, cycleLimit, clazz);
        return ret;
    }

    //queryService without payload
    public <T> T queryService(@NonNull String serviceName, @NonNull String method, Class<T> clazz) throws IOException {

        T ret = this.queryService(serviceName, method, "", null, mutaRequestOption.caller, null, null, clazz);
        return ret;
    }

    //queryService with given payload
    public <T, P> T queryService(@NonNull String serviceName, @NonNull String method, @NonNull P payloadData, Class<T> clazz) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);
        T ret = this.queryService(serviceName, method, payload, null, mutaRequestOption.caller, null, null, clazz);
        return ret;
    }

    // sign and send transaction
    public String sendTransaction(String chainId, String cyclesLimit, String cyclesPrice, String nonce, String timeout, @NonNull String serviceName, @NonNull String method, String payload) throws IOException {

        RawTransaction rawTransaction = this.compose(
                chainId,
                cyclesLimit,
                cyclesPrice,
                nonce,
                timeout,
                serviceName,
                method,
                payload
        );

        byte[] txHash = Util.keccak256(rawTransaction.encode());

        byte[] sig = this.account.sign(txHash);

        TransactionEncryption transactionEncryption = this.signTransaction(rawTransaction);

        String ret = this.client.sendTransaction(rawTransaction, transactionEncryption);
        return ret;
    }

    //sendTransaction only with serviceName, method and payloadData
    public <P> String sendTransaction(@NonNull String serviceName, @NonNull String method, P payloadData) throws IOException {
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

    public String sendTransaction(RawTransaction rawTransaction, TransactionEncryption transactionEncryption) throws IOException {
        String ret = this.client.sendTransaction(rawTransaction, transactionEncryption);
        return ret;
    }


    public <P> P getReceiptSucceedData(String txHash, Class<P> clazz) throws IOException {
        Receipt receipt = client.getReceipt(txHash);

        P ret = objectMapper.readValue(receipt.response.response.succeedData, clazz);
        return ret;
    }

    public <P> P getReceiptSucceedDataRetry(String txHash, Class<P> clazz) throws IOException {
        int times = 10;
        while (times > 0) {

            try {

                P ret = getReceiptSucceedData(txHash, clazz);
                return ret;
            } catch (IOException e) {

            }

            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }

            times--;
        }

        throw new IOException("getReceipt() fails to retrieve tx data");
    }

    public <P> RawTransaction compose(@NonNull String serviceName, @NonNull String method, P payloadData) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);
        return compose(null,
                null,
                null,
                null,
                null,
                serviceName,
                method,
                payload
        );
    }

    public RawTransaction compose(String chainId, String cyclesLimit, String cyclesPrice, String nonce, String timeout, @NonNull String serviceName, @NonNull String method, String payload) throws IOException {

        if (chainId == null) {
            chainId = mutaRequestOption.chainId;
        }
        if (cyclesLimit == null) {
            cyclesLimit = mutaRequestOption.cyclesLimit;
        }
        if (cyclesPrice == null) {
            cyclesPrice = mutaRequestOption.cyclesPrice;
        }
        if (nonce == null) {
            nonce = Util.generateRandom32BytesHex();
        }

        if (timeout == null) {
            BigInteger latestHeight = this.client.getLatestHeight();
            timeout = Util.start0x(new BigInteger(Util.remove0x(mutaRequestOption.timeout), 16).add(latestHeight).toString(16));
        }
        if (payload == null) {
            payload = "";
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
                Util.start0x(account.address)
        );

        return rawTransaction;
    }

    public TransactionEncryption signTransaction(RawTransaction rawTransaction){
        byte[] txHash = Util.keccak256(rawTransaction.encode());

        byte[] sig = this.account.sign(txHash);

        TransactionEncryption transactionEncryption = new TransactionEncryption(
                Util.start0x(account.publicKey),
                Util.start0x(Hex.toHexString(sig)),
                Util.start0x(Hex.toHexString(txHash))
        );

        return transactionEncryption;
    }

    public TransactionEncryption appendSignedTransaction(RawTransaction rawTransaction, TransactionEncryption transactionEncryption) throws IOException {
        byte[] txHash = Util.keccak256(rawTransaction.encode());
        if(!Util.start0x(Hex.toHexString(txHash)).equals(transactionEncryption.txHash)){
            throw new IOException("RawTransaction's txHash and TransactionEncryption's doesn't match");
        }

        TransactionEncryption signed = this.signTransaction(rawTransaction);

        transactionEncryption.appendSignatureAndPubkey(signed);

        return transactionEncryption;
    }
}
