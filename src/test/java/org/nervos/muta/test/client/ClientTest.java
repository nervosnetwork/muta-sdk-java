package org.nervos.muta.test.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import org.junit.jupiter.api.*;
import org.nervos.muta.Muta;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.graphql_schema.*;
import org.nervos.muta.client.type.primitive.U64;
import org.nervos.muta.service.asset.AssetService;
import org.nervos.muta.service.asset.type.Asset;
import org.nervos.muta.service.asset.type.CreateAssetPayload;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientTest {

    public static Client client;
    public static GHash gHash;

    public ClientTest() {
        client = Client.defaultClient();
    }

    @Test
    @Order(1)
    public void getBlock() throws IOException {
        Block block = client.getBlock(GUint64.ZERO);
        Assertions.assertEquals(BigInteger.ZERO, block.getHeader().getHeight().get());
    }

    @Test
    @Order(2)
    public void getLatestBlock() throws IOException {
        Block block = client.getBlock(null);
        Assertions.assertNotNull(block.getHeader());
    }

    @Test
    @Order(3)
    public void doPrepareAndGetReceiptSucceedDataRetry() throws IOException {
        Muta muta = Muta.defaultMuta();
        gHash =
                muta.sendTransaction(
                        AssetService.SERVICE_NAME,
                        AssetService.METHOD_CREATE_ASSET,
                        new CreateAssetPayload("Squirrel", "SQU", U64.fromLong(1314)));

        muta.getReceiptSucceedDataRetry(gHash, new TypeReference<Asset>() {}, new ArrayList<>());
    }

    @Test
    @Order(4)
    public void getTransaction() throws IOException {
        SignedTransaction tx = client.getTransaction(gHash);
        Assertions.assertNotNull(tx);
        Assertions.assertEquals(tx.getTxHash(), gHash);
    }

    @Test
    @Order(5)
    public void getReceipt() throws IOException {
        Receipt receipt = client.getReceipt(gHash);
        Assertions.assertNotNull(receipt);
        Assertions.assertEquals(receipt.getTxHash(), gHash);
    }
}
