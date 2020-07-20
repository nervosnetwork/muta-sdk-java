package org.nervos.muta;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.MutaRequestOption;
import org.nervos.muta.client.type.graphql_schema.*;
import org.nervos.muta.client.type.primitive.Bytes;
import org.nervos.muta.wallet.Account;
import org.nervos.muta.wallet.Wallet;

@Slf4j
public class Example {
    public static void main(String[] args) throws IOException {
        Wallet wallet =
                Wallet.from_mnemonic(
                        "drastic behave exhaust enough tube judge real logic escape critic horror gold");
        log.info("Wallet's seed: " + Bytes.fromByteArray(wallet.getSeed()).toString());

        Account account = wallet.derive(60, 0);
        log.info("Account's private key: " + account.getPrivateKeyHex());
        log.info("Account's public key: " + account.getPublicKeyHex());
        log.info("Account's address: " + account.getAddressHex());

        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
        // now you make a client to talk in GraphQl grammar via HTTP over TCP/IP
        Client client = new Client("http://localhost:8000/graphql", okHttpClient);

        // get a block
        Block block = client.getBlock(GUint64.fromLong(0));

        // get a latest block
        block = client.getBlock(null);

        // get a transaction
        SignedTransaction signedTransaction =
                client.getTransaction(
                        GHash.fromHexString(
                                "0x0000000000000000000000000000000000000000000000000000000000000000"));

        // get a receipt
        Receipt receipt =
                client.getReceipt(
                        GHash.fromHexString(
                                "0x0000000000000000000000000000000000000000000000000000000000000000"));

        // you don't need manually call a query, marshall and unmarshall data, Muta is the tool for
        // you
        Muta muta = new Muta(client, account, MutaRequestOption.defaultMutaRequestOption());

        // run a query with given payload and auto parse JSON string back to java object.
        // you can use generic to customize the 3nd and 4th param.
        Object result =
                muta.queryService(
                        "service-name", "method", new Object(), new TypeReference<Object>() {});

        // send a tx and get txHash for further use
        GHash txHahs = muta.sendTransaction("service-name", "method", new Object());

        // compose a tx, but do not sign it
        InputRawTransaction inputRawTransaction =
                muta.compose(
                        "service-name",
                        "method",
                        new Object(),
                        GAddress.fromHexString("0x0000000000000000000000000000000000000000"));

        // sign a tx and get signature
        InputTransactionEncryption sig1 = muta.signTransaction(inputRawTransaction);

        // or if you want to use Multi-Sig, combine signature together
        // first we get another Muta of different account
        Muta muta_another = Muta.defaultMuta();

        InputTransactionEncryption sig2 = muta_another.signTransaction(inputRawTransaction);

        // now sig1 contains sig2
        sig1.appendSignatureAndPubkey(sig2);

        // or you can sign a tx and combine with another sig1 in one step
        InputTransactionEncryption combinedSignature =
                muta_another.appendSignedTransaction(inputRawTransaction, sig1);

        // due to tx is async, you may need polling the receipt to get the response in one call
        Object response =
                muta.sendTransactionAndPollResult(
                        "service-name", "method", new Object(), new TypeReference<Object>() {});
    }
}
