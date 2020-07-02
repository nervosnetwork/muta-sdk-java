package org.nervos.muta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.MutaRequestOption;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Bytes;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;
import org.nervos.muta.client.type.graphql_schema_scalar.Uint64;
import org.nervos.muta.client.type.request.InputRawTransaction;
import org.nervos.muta.client.type.request.InputTransactionEncryption;
import org.nervos.muta.client.type.response.Block;
import org.nervos.muta.client.type.response.Receipt;
import org.nervos.muta.client.type.response.ServiceResponse;
import org.nervos.muta.exception.GraphQlError;
import org.nervos.muta.exception.ReceiptResponseError;
import org.nervos.muta.exception.ServiceResponseError;
import org.nervos.muta.util.Util;
import org.nervos.muta.wallet.Account;

@Getter
@Slf4j
public class Muta {
  private final Client client;
  private final Account account;
  private final MutaRequestOption mutaRequestOption;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public Muta(Client client, Account account, MutaRequestOption defaultReqOption) {

    if (client == null) {
      log.warn("you are in offline mode");
    }

    if (account == null) {
      account = Account.defaultAccount();
      log.warn("you are using default account: " + account.getAddressByteArray());
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

  public BigInteger getLatestHeight() throws IOException {
    checkClient();
    Block block = client.getBlock(null);
    return new BigInteger(Util.remove0x(block.getHeader().getHeight()), 16);
  }

  // queryService without payload
  public <T> T queryService(
      @NonNull String serviceName, @NonNull String method, TypeReference<T> tr) throws IOException {
    // we directly give a "null" JSON
    T ret = this.queryService(serviceName, method, "null", null, null, null, null, tr);
    return ret;
  }

  // queryService with given payload
  public <T, P> T queryService(
      @NonNull String serviceName, @NonNull String method, P payloadData, TypeReference<T> tr)
      throws IOException {
    // null pointer will marshal to null
    String payload = this.objectMapper.writeValueAsString(payloadData);

    T ret = this.queryService(serviceName, method, payload, null, null, null, null, tr);
    return ret;
  }

  public <T> T queryService(
      @NonNull String serviceName,
      @NonNull String method,
      @NonNull String payload,
      Uint64 height,
      Address caller,
      Uint64 cyclePrice,
      Uint64 cycleLimit,
      TypeReference<T> tr)
      throws IOException {
    if (caller == null) {
      caller = mutaRequestOption.getCaller();
    }
    checkClient();

    ServiceResponse serviceResponse =
        client.queryService(serviceName, method, payload, height, caller, cyclePrice, cycleLimit);
    T ret = parseServiceResponse(serviceResponse, tr);
    return ret;
  }

  // this is the commonly used sendTransaction
  // sendTransaction only with serviceName, method and payloadData
  public <P> Hash sendTransaction(
      @NonNull String serviceName, @NonNull String method, P payloadData) throws IOException {
    String payload = this.objectMapper.writeValueAsString(payloadData);

    return this.sendTransaction(null, null, null, null, null, serviceName, method, payload);
  }

  // sign and send transaction
  public Hash sendTransaction(
      Hash chainId,
      Uint64 cyclesLimit,
      Uint64 cyclesPrice,
      Hash nonce,
      Uint64 timeout,
      @NonNull String serviceName,
      @NonNull String method,
      String payload)
      throws IOException {

    InputRawTransaction inputRawTransaction =
        this.compose(
            chainId, cyclesLimit, cyclesPrice, nonce, timeout, serviceName, method, payload, null);

    byte[] encoded = inputRawTransaction.encode();

    byte[] txHash = Util.keccak256(encoded);

    InputTransactionEncryption inputTransactionEncryption =
        this.signTransaction(inputRawTransaction);

    checkClient();

    Hash ret = this.sendTransaction(inputRawTransaction, inputTransactionEncryption);
    return ret;
  }

  public Hash sendTransaction(
      InputRawTransaction inputRawTransaction,
      InputTransactionEncryption inputTransactionEncryption)
      throws IOException {
    checkClient();
    Hash ret = this.client.sendTransaction(inputRawTransaction, inputTransactionEncryption);
    return ret;
  }

  public <P, R> R sendTransactionAndPollResult(
      @NonNull String serviceName, @NonNull String method, P payloadData, TypeReference<R> tr)
      throws IOException {
    String payload = this.objectMapper.writeValueAsString(payloadData);

    Hash txHash = this.sendTransaction(null, null, null, null, null, serviceName, method, payload);
    log.debug("send txhash: " + txHash);
    return getReceiptSucceedDataRetry(txHash, tr);
  }

  // while receipt is ready but the service response is error, thrown runtime exception
  public <R> R getReceiptSucceedData(Hash txHash, TypeReference<R> tr) throws IOException {
    checkClient();

    Receipt receipt = client.getReceipt(txHash);

    if (!BigInteger.ZERO.equals(
        new BigInteger(Util.remove0x(receipt.getResponse().getResponse().getCode()), 16))) {
      log.debug("getReceiptSucceedData error: " + objectMapper.writeValueAsString(receipt));
      throw new ReceiptResponseError(
          receipt.getResponse().getServiceName(),
          receipt.getResponse().getMethod(),
          receipt.getResponse().getResponse().getCode(),
          receipt.getResponse().getResponse().getErrorMessage());
    }

    try {
      R ret = parseServiceResponse(receipt.getResponse().getResponse(), tr);
      return ret;
    } catch (ServiceResponseError e) {
      throw new ReceiptResponseError(
          receipt.getResponse().getServiceName(),
          receipt.getResponse().getMethod(),
          receipt.getResponse().getResponse().getCode(),
          receipt.getResponse().getResponse().getErrorMessage());
    } catch (IOException e) {
      throw e;
    }
  }

  public <P> P getReceiptSucceedDataRetry(Hash txHash, TypeReference<P> tr) throws IOException {
    int times = this.mutaRequestOption.getPolling_times();
    Exception lastErr = null;
    while (times > 0) {

      try {

        P ret = getReceiptSucceedData(txHash, tr);
        return ret;
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

  public <P> InputRawTransaction compose(
      @NonNull String serviceName, @NonNull String method, P payloadData, Address sender)
      throws IOException {
    String payload = this.objectMapper.writeValueAsString(payloadData);
    return compose(null, null, null, null, null, serviceName, method, payload, sender);
  }

  public InputRawTransaction compose(
      Hash chainId,
      Uint64 cyclesLimit,
      Uint64 cyclesPrice,
      Hash nonce,
      Uint64 timeout,
      @NonNull String serviceName,
      @NonNull String method,
      String payload,
      Address sender)
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
      nonce = Hash.fromHexString(Util.generateRandom32BytesHex());
    }

    if (timeout == null) {
      checkClient();
      BigInteger latestHeight = this.getLatestHeight();
      timeout = Uint64.fromBigInteger(mutaRequestOption.getTimeout().get().add(latestHeight));
    }
    if (payload == null) {
      payload = "";
    }

    if (sender == null) {
      sender = account.getAddress();
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

  // sign a raw transaction, do nothing more
  public InputTransactionEncryption signTransaction(InputRawTransaction inputRawTransaction) {
    byte[] txHash = Util.keccak256(inputRawTransaction.encode());

    byte[] sig = this.account.sign(txHash);

    InputTransactionEncryption inputTransactionEncryption =
        new InputTransactionEncryption(
            Bytes.fromByteArray(account.getPublicKeyByteArray()),
            Bytes.fromByteArray(sig),
            Bytes.fromByteArray(txHash));

    return inputTransactionEncryption;
  }

  // sign a raw transaction and append your sig to given transaction sig (transactionEncryption)
  public InputTransactionEncryption appendSignedTransaction(
      InputRawTransaction inputRawTransaction,
      InputTransactionEncryption inputTransactionEncryption)
      throws IOException {
    byte[] txHash = Util.keccak256(inputRawTransaction.encode());
    if (!Bytes.fromByteArray(txHash).equals(inputTransactionEncryption.txHash)) {
      throw new IOException("RawTransaction's txHash and TransactionEncryption's doesn't match");
    }

    InputTransactionEncryption signed = this.signTransaction(inputRawTransaction);

    inputTransactionEncryption.appendSignatureAndPubkey(signed);

    return inputTransactionEncryption;
  }

  private void checkClient() throws RuntimeException {
    if (this.client == null) {
      throw new RuntimeException("the client of Muta instance hasn't been set");
    }
  }

  // parse ServiceResponse's succeedData into class
  protected <T> T parseServiceResponse(ServiceResponse serviceResponse, TypeReference<T> tr)
      throws IOException {
    if (!new BigInteger(Util.remove0x(serviceResponse.getCode())).equals(BigInteger.ZERO)) {
      throw new ServiceResponseError(serviceResponse.getCode(), serviceResponse.getErrorMessage());
    }

    String succeedMsg = serviceResponse.getSucceedData();

    // it's weird design for muta
    if ("".equals(succeedMsg)) {
      succeedMsg = "null";
    }

    T ret = objectMapper.readValue(succeedMsg, tr);
    return ret;
  }
}
