package org.nervos.muta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.MutaRequestOption;
import org.nervos.muta.client.type.ParsedServiceResponse;
import org.nervos.muta.client.type.graphql_schema.*;
import org.nervos.muta.exception.GraphQlError;
import org.nervos.muta.exception.TxBeforeHookError;
import org.nervos.muta.util.CryptoUtil;
import org.nervos.muta.util.Util;
import org.nervos.muta.wallet.Account;

/**
 * Muta is a comprehensive class in front of all worker class
 *
 * @author Lycrus Hamster
 */
@Getter
@Slf4j
public class Muta {
    /** Client is on duty of GraphQl communication */
    private final Client client;
    /** Account handles all jobs of signing */
    private final Account account;
    /** Where options lies */
    private final MutaRequestOption mutaRequestOption;
    /** Jackson's JSON marshall-er */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * A hook before calling {@link
     * org.nervos.muta.client.Client#sendTransaction(InputRawTransaction,
     * InputTransactionEncryption)} You can pass a closure/lambda expression to do some Predication
     * work You can set, unset it whenever
     */
    @Setter private Predicate<Muta> sendTxBeforeHook;

    /**
     * Constructor with all params you can customize
     *
     * @param client client {@link org.nervos.muta.Muta#client}
     * @param account account {@link org.nervos.muta.Muta#account}
     * @param defaultReqOption options {@link org.nervos.muta.Muta#mutaRequestOption}
     */
    public Muta(Client client, Account account, MutaRequestOption defaultReqOption) {

        if (client == null) {
            log.warn("you are in offline mode");
        }

        if (account == null) {
            account = Account.defaultAccount();
            log.warn("you are using default account: " + account.getAddressHex());
        }

        if (defaultReqOption == null) {
            defaultReqOption = MutaRequestOption.defaultMutaRequestOption();
        }

        this.mutaRequestOption = defaultReqOption;
        this.account = account;
        this.client = client;
    }

    /** @return {@link org.nervos.muta.Muta} */
    public static Muta defaultMuta() {
        return new Muta(Client.defaultClient(), null, null);
    }

    /**
     * Directly send Graph query to get the latest mined block's height
     *
     * @return Block height, literally
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public BigInteger getLatestHeight() throws IOException {
        checkClient();
        Block block = client.getBlock(null);
        return block.getHeader().getHeight().get();
    }

    /**
     * Start a GetBlock GraphQl query
     *
     * @param height The height want to query, <b>leave null for the latest</b>
     * @return The block info, note the block could be null
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public Block getBlock(GUint64 height) throws IOException {
        return client.getBlock(height);
    }

    /**
     * Start a GetReceipt GraphQl query
     *
     * @param txHash The transaction hash of transaction you want to query
     * @return The Receipt of the transaction's execution result, maybe null
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public Receipt getReceipt(GHash txHash) throws IOException {
        return client.getReceipt(txHash);
    }

    /**
     * Start a GetTransaction GraphQl query
     *
     * @param txHash The transaction hash of transaction you want to query
     * @return The SignedTransaction when it sends, maybe null
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public SignedTransaction getTransaction(GHash txHash) throws IOException {
        return client.getTransaction(txHash);
    }

    /**
     * Send GraphQl queryService query without payload
     *
     * @param serviceName The name of the service
     * @param method The method name of the service
     * @param tr TypeReference to hold type param
     * @param <T> type param to auto unmarshall JSON string
     * @return unmarshalled object of type T
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public <T> T queryService(
            @NonNull String serviceName, @NonNull String method, TypeReference<T> tr)
            throws IOException {
        // we directly give a "null" JSON
        T ret = this.queryService(serviceName, method, "null", null, null, null, null, tr);
        return ret;
    }

    /**
     * Send GraphQl queryService query with given payload
     *
     * @param serviceName The name of the service
     * @param method The method name of the service
     * @param payloadData The payload data, it will be automatically marshalled to JSON string
     * @param tr TypeReference to hold type param
     * @param <T> type param to auto unmarshall JSON string
     * @param <P> type param to aulto marshal JSON string, the type param should be friendly with
     *     Jackson
     * @return unmarshalled object of type T
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public <T, P> T queryService(
            @NonNull String serviceName, @NonNull String method, P payloadData, TypeReference<T> tr)
            throws IOException {
        // null pointer will marshal to null
        String payload = this.objectMapper.writeValueAsString(payloadData);

        T ret = this.queryService(serviceName, method, payload, null, null, null, null, tr);
        return ret;
    }

    /**
     * Send GraphQl queryService query with given payload and more detailed params
     *
     * @param serviceName The name of the service
     * @param method The method name of the service
     * @param payload The payload data string, should be marshalled JSON string
     * @param tr TypeReference to hold type param
     * @param <T> type param to auto unmarshall JSON string
     * @param height On which height this queryService should run
     * @param caller in the name of caller this queryService should run
     * @param cyclePrice give a specified cyclePrice
     * @param cycleLimit give a specified cycleLimit
     * @return unmarshalled object of type T, or null for error in ServiceResponse
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public <T> T queryService(
            @NonNull String serviceName,
            @NonNull String method,
            @NonNull String payload,
            GUint64 height,
            GAddress caller,
            GUint64 cyclePrice,
            GUint64 cycleLimit,
            TypeReference<T> tr)
            throws IOException {
        if (caller == null) {
            caller = mutaRequestOption.getCaller();
        }
        checkClient();

        ServiceResponse serviceResponse =
                client.queryService(
                        serviceName, method, payload, height, caller, cyclePrice, cycleLimit);
        ParsedServiceResponse<T> parsedServiceResponse = parseServiceResponse(serviceResponse, tr);
        T ret = parsedServiceResponse.isError() ? null : parsedServiceResponse.getSucceedData();
        return ret;
    }

    /**
     * Send a transaction and return its transaction hash, this is the commonly used
     * sendTransaction(), sendTransaction() only with serviceName, method and payloadData
     *
     * @param serviceName The name of the service
     * @param method The method name of the service
     * @param payloadData The payload data, it will be automatically marshalled to JSON string
     * @param <P> type param to aulto marshal JSON string, the type param should be friendly with
     *     Jackson
     * @return Transaction Hash
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public <P> GHash sendTransaction(
            @NonNull String serviceName, @NonNull String method, P payloadData) throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);

        return this.sendTransaction(null, null, null, null, null, serviceName, method, payload);
    }

    /**
     * Send a transaction and return its transaction hash, you can set all params by this function.
     * Otherwise, chose {@link org.nervos.muta.Muta#sendTransaction(String, String, Object)}
     *
     * @param chainId ChainId of the Muta chain
     * @param cyclesLimit cyclesLimit you want to set
     * @param cyclesPrice cyclesPrice you want to set
     * @param nonce you can set an random nonce as you wish
     * @param timeout set a timeout for this block
     * @param serviceName the name of the service
     * @param method the method name
     * @param payload the payload for the method
     * @return transaction hash
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public GHash sendTransaction(
            GHash chainId,
            GUint64 cyclesLimit,
            GUint64 cyclesPrice,
            GHash nonce,
            GUint64 timeout,
            @NonNull String serviceName,
            @NonNull String method,
            String payload)
            throws IOException {

        InputRawTransaction inputRawTransaction =
                this.compose(
                        chainId,
                        cyclesLimit,
                        cyclesPrice,
                        nonce,
                        timeout,
                        serviceName,
                        method,
                        payload,
                        null);

        InputTransactionEncryption inputTransactionEncryption =
                this.signTransaction(inputRawTransaction);

        checkClient();

        GHash ret = this.sendTransaction(inputRawTransaction, inputTransactionEncryption);
        return ret;
    }

    /**
     * Send a transaction and return its transaction hash, by a DEFINED Raw Transaction and its
     * Encryption(signature). This method allows you to use composed Tx and sign it offline, or use
     * the signature to do more things, like Multi-Sig
     *
     * @param inputRawTransaction The transaction, literally
     * @param inputTransactionEncryption The signature of the transaction to pass to Muta Chain to
     *     verify, this could be a Multi-Sig
     * @return Transaction hash
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public GHash sendTransaction(
            InputRawTransaction inputRawTransaction,
            InputTransactionEncryption inputTransactionEncryption)
            throws IOException {
        checkClient();

        // hook
        if (sendTxBeforeHook != null && !sendTxBeforeHook.test(this)) {
            throw new TxBeforeHookError("TxBeforeHook predicates fails");
        }

        GHash ret = this.client.sendTransaction(inputRawTransaction, inputTransactionEncryption);
        return ret;
    }

    /**
     * Send a transaction with commonly used param and poll the result of the execution, a.k.a.
     * receipt. And then poll the receipt to get the receipt and then return it.
     *
     * @param serviceName the name of the service
     * @param method the method of the service
     * @param payloadData the payload used in the method, will be marshalled by Jackson to JSON
     *     string,
     * @param tr Type reference to hold type param
     * @param <P> The generic of payload, should be friendly with Jackson
     * @param <R> The generic of return data, should be friendly with Jackson
     * @return The unmarshalled java object, or null for error returned in ServiceResponse
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public <P, R> R sendTransactionAndPollResult(
            @NonNull String serviceName, @NonNull String method, P payloadData, TypeReference<R> tr)
            throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);

        GHash txHash =
                this.sendTransaction(null, null, null, null, null, serviceName, method, payload);
        log.debug("send txhash: " + txHash);
        ParsedServiceResponse<R> parsedServiceResponse = getReceiptSucceedDataRetry(txHash, tr);
        return parsedServiceResponse.isError() ? null : parsedServiceResponse.getSucceedData();
    }

    /**
     * Poll the receipt of the given transaction hash, unmarshall it to java object
     *
     * @param txHash the transaction hash you want to poll
     * @param tr Type reference to hold type param
     * @param <P> The generic of return data
     * @return The java object or error
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public <P> ParsedServiceResponse<P> getReceiptSucceedDataRetry(
            GHash txHash, TypeReference<P> tr) throws IOException {
        int times = this.mutaRequestOption.getPolling_times();
        Exception lastErr = null;
        while (times > 0) {
            try {
                ParsedServiceResponse<P> ret = getReceiptSucceedData(txHash, tr);
                if (ret != null) {
                    return ret;
                }
            } catch (GraphQlError e) {
                lastErr = e;
                log.debug("getReceiptSucceedDataRetry: " + e.getMessage());
            } catch (Exception e) {
                lastErr = e;
                log.error("getReceiptSucceedDataRetry: " + e.getMessage());
            }

            try {
                Thread.sleep(this.mutaRequestOption.getPolling_interval());
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }

            times--;
        }

        throw new IOException(
                "getReceipt() fails to retrieve tx data by "
                        + this.mutaRequestOption.getPolling_times()
                        + " times, last error: ",
                lastErr);
    }
    //

    /**
     * Unmarshall the JSON string according to the given type param. While receipt is ready but the
     *
     * @param txHash The transaction hash
     * @param tr Type reference to hold type param
     * @param <R> The generic of the return data to be unmarshalled
     * @return Return null for receipt is null(maybe not ready), return ParsedServiceResponse if get
     *     receipt
     * @throws IOException Exception, maybe HTTP/network error, or GraphQl execution failure
     */
    public <R> ParsedServiceResponse<R> getReceiptSucceedData(
            GHash txHash, TypeReference<R> tr /*add event here*/) throws IOException {
        checkClient();

        Receipt receipt = client.getReceipt(txHash);

        if (receipt == null) {
            return null;
        }

        ParsedServiceResponse<R> ret =
                parseServiceResponse(receipt.getResponse().getResponse(), tr);

        return ret;
    }

    /**
     * Use the given params to compose a Transaction, you can use this method to compose Transaction
     * offline.
     *
     * @param serviceName the name of the service
     * @param method the method of the service
     * @param payloadData the payload to be used by method
     * @param sender Who sends this
     * @param <P> The type param of the payloadData, should be friendly with Jackson
     * @return The composed transaction
     * @throws IOException JSON marshal exception
     */
    public <P> InputRawTransaction compose(
            @NonNull String serviceName, @NonNull String method, P payloadData, GAddress sender)
            throws IOException {
        String payload = this.objectMapper.writeValueAsString(payloadData);
        return compose(null, null, null, null, null, serviceName, method, payload, sender);
    }

    /**
     * Verbose version of {@link org.nervos.muta.Muta#compose(String, String, Object, GAddress)}
     *
     * @param chainId ChainId of Muta Chain
     * @param cyclesLimit Specific cyclesLimit
     * @param cyclesPrice Specific cyclesPrice
     * @param nonce You can set the random nonce as you wish
     * @param timeout Timeout option of transaction
     * @param serviceName name of service
     * @param method method of service
     * @param payload payload of method
     * @param sender who sends the transaction
     * @return Composed transaction
     * @throws IOException JSON marshal exception
     */
    public InputRawTransaction compose(
            GHash chainId,
            GUint64 cyclesLimit,
            GUint64 cyclesPrice,
            GHash nonce,
            GUint64 timeout,
            @NonNull String serviceName,
            @NonNull String method,
            String payload,
            GAddress sender)
            throws IOException {

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
            nonce = GHash.fromHexString(Util.generateRandom32BytesHex());
        }

        if (timeout == null) {
            checkClient();
            BigInteger latestHeight = this.getLatestHeight();
            timeout =
                    GUint64.fromBigInteger(mutaRequestOption.getTimeout().get().add(latestHeight));
        }
        if (payload == null) {
            payload = "";
        }

        if (sender == null) {
            sender = account.getGAddress();
        }

        InputRawTransaction inputRawTransaction =
                new InputRawTransaction(
                        chainId,
                        cyclesLimit,
                        cyclesPrice,
                        nonce,
                        method,
                        payload,
                        serviceName,
                        timeout,
                        sender);

        return inputRawTransaction;
    }

    /**
     * Manually sign a transaction and return the signature
     *
     * @param inputRawTransaction The transaction to be signed
     * @return Signed signature
     */
    public InputTransactionEncryption signTransaction(InputRawTransaction inputRawTransaction) {
        byte[] txHash = Util.keccak256(inputRawTransaction.encode());

        byte[] sig = this.account.sign(txHash);

        InputTransactionEncryption inputTransactionEncryption =
                new InputTransactionEncryption(
                        GBytes.fromByteArray(account.getPublicKeyByteArray()),
                        GBytes.fromByteArray(sig),
                        GBytes.fromByteArray(txHash));

        return inputTransactionEncryption;
    }

    // sign a raw transaction and append your sig to given transaction sig (transactionEncryption)

    /**
     * This method use internal account info to sign transaction, and append the signed signature
     * together with the given signature, a.k.a. Multi-Sig
     *
     * @param inputRawTransaction The transaction to be signed
     * @param inputTransactionEncryption The signature to be appended with
     * @return Appended Signature, a.k.a. Multi-Sig
     * @throws IOException JSON marshal exception
     */
    public InputTransactionEncryption appendSignedTransaction(
            InputRawTransaction inputRawTransaction,
            InputTransactionEncryption inputTransactionEncryption)
            throws IOException {
        byte[] txHash = Util.keccak256(inputRawTransaction.encode());
        if (!GBytes.fromByteArray(txHash).equals(inputTransactionEncryption.txHash)) {
            throw new IOException(
                    "RawTransaction's txHash and TransactionEncryption's doesn't match");
        }

        InputTransactionEncryption signed = this.signTransaction(inputRawTransaction);

        inputTransactionEncryption.appendSignatureAndPubkey(signed);

        return inputTransactionEncryption;
    }

    /** Simple check if client is set, a.k.a. online mode. */
    private void checkClient() {
        if (this.client == null) {
            throw new RuntimeException("the client of Muta instance hasn't been set");
        }
    }

    /**
     * Unmarshall ServiceResponse's succeedData, which should be JSON string, into class by type
     * param.
     *
     * @param serviceResponse ServiceResponse to be parsed
     * @param tr Type reference to hold type param
     * @param <T> generic to indicate how to unmarshall JSON string
     * @return Unmarshalled java object
     * @throws IOException JSON unmarshall error or service response error
     */
    protected <T> ParsedServiceResponse<T> parseServiceResponse(
            ServiceResponse serviceResponse, TypeReference<T> tr) throws IOException {

        return ParsedServiceResponse.fromServiceResponse(serviceResponse, tr);
    }

    /**
     * do ecdsa recovery of secp256k1
     *
     * @param signature the signature, combined with r and s, respective of 32 bytes
     * @param msgHash the digest of message, which is 32 bytes, calc-ed by keccak256
     * @param targetAddress which address you assume the signature is for
     * @return match or not
     */
    public boolean ec_recover(
            @NonNull byte[] signature, @NonNull byte[] msgHash, @NonNull byte[] targetAddress) {
        if (msgHash.length != 32) {
            return false;
        }
        if (signature.length != 64) {
            return false;
        }
        return CryptoUtil.recovery(signature, msgHash, targetAddress);
    }

    /**
     * do ecdsa verification of secp256k1
     *
     * @param signature the signature, combined with r and s, respective of 32 bytes
     * @param msgHash the digest of message, which is 32 bytes, calc-ed by keccak256
     * @param publicKey the public key which signs the signature
     * @return match or not
     */
    public boolean ec_verify(
            @NonNull byte[] signature, @NonNull byte[] msgHash, @NonNull byte[] publicKey) {
        if (msgHash.length != 32) {
            return false;
        }
        if (signature.length != 64) {
            return false;
        }
        return CryptoUtil.verify(signature, msgHash, publicKey);
    }
}
