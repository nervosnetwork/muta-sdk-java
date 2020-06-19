package org.nervos.muta;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.MutaRequestOption;
import org.nervos.muta.client.type.request.SendTransactionRequest;
import org.nervos.muta.util.Util;
import org.nervos.muta.wallet.Account;

import java.io.IOException;

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

    public <T> T queryService(@NonNull String serviceName, @NonNull String method, Class<T> clazz) throws IOException {

        T ret = this.queryService(serviceName, method, "", null, mutaRequestOption.caller, null, null, clazz);
        return ret;
    }

    public <T, P> T queryService(@NonNull String serviceName, @NonNull String method, @NonNull P payloadData, Class<T> clazz) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);
        T ret = this.queryService(serviceName, method, payload, null, mutaRequestOption.caller, null, null, clazz);
        return ret;
    }

    public String sendTransaction(String chainId, String cyclesLimit, String cyclesPrice, String nonce, String timeout, @NonNull String serviceName, @NonNull String method, String payload) throws IOException {
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
            timeout = mutaRequestOption.timeout;
        }
        if (payload == null) {
            payload = "";
        }

        SendTransactionRequest.InputRawTransaction inputRawTransaction = new SendTransactionRequest.InputRawTransaction(
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

        byte[] txHash = Util.keccak256(inputRawTransaction.encode());

        System.out.println("txHash: " + Hex.toHexString(txHash));

        byte[] sig = this.account.sign(txHash);

        SendTransactionRequest.InputTransactionEncryption inputTransactionEncryption = new SendTransactionRequest.InputTransactionEncryption(
                Util.start0x(account.publicKey),
                Util.start0x(Hex.toHexString(sig)),
                Util.start0x(Hex.toHexString(txHash))
        );

        System.out.println(inputTransactionEncryption);

        String ret = this.client.sendTransaction(inputRawTransaction, inputTransactionEncryption);
        return ret;
    }

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
}
