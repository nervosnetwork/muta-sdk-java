package org.nervos.muta.test.service.asset;

import java.io.IOException;
import java.math.BigInteger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.nervos.muta.Muta;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.client.type.graphql_schema.GHash;
import org.nervos.muta.client.type.primitive.U64;
import org.nervos.muta.service.asset.AssetService;
import org.nervos.muta.service.asset.type.*;
import org.nervos.muta.wallet.Account;

@Slf4j
@Data
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AssetServiceTest {

    private static String ASSET_NAME = "Hamster Coin";
    private static String ASSET_SYMBOL = "HAM";
    private static U64 ASSET_SUPPLY = U64.fromBigInteger(BigInteger.valueOf(1000L));
    private static GAddress EMPTY_ADDRESS =
            GAddress.fromHexString("0x0000000000000000000000000000000000000000");

    private static AssetService assetService;
    private static AssetService backupAssetService;

    private static GHash asset_id;

    private static GAddress issuer;

    private static GAddress another_account_address;

    public AssetServiceTest() {
        assetService = new AssetService(Muta.defaultMuta());

        backupAssetService =
                new AssetService(new Muta(Client.defaultClient(), Account.generate(), null));

        issuer = assetService.getMuta().getAccount().getAddress();
        another_account_address = backupAssetService.getMuta().getAccount().getAddress();
    }

    @Test
    @Order(1)
    public void createAsset() throws IOException {
        Asset asset =
                assetService.createAsset(
                        new CreateAssetPayload(ASSET_NAME, ASSET_SYMBOL, ASSET_SUPPLY));
        Assertions.assertEquals(ASSET_SUPPLY, asset.getSupply());
        Assertions.assertEquals(issuer, asset.getIssuer().toGAdress());
        Assertions.assertEquals(ASSET_SYMBOL, asset.getSymbol());
        asset_id = asset.getId().toGHash();
        log.info("asset id: " + asset_id);
    }

    @Test
    @Order(2)
    public void getAsset() throws IOException {
        Asset asset = assetService.getAsset(new GetAssetPayload(asset_id.toHash()));
        Assertions.assertEquals(ASSET_NAME, asset.getName());
    }

    @Test
    @Order(3)
    public void getBalance() throws IOException {
        U64 balance =
                assetService
                        .getBalance(new GetBalancePayload(asset_id.toHash(), issuer.toAddress()))
                        .getBalance();

        Assertions.assertEquals(ASSET_SUPPLY, balance);

        balance =
                assetService
                        .getBalance(
                                new GetBalancePayload(asset_id.toHash(), EMPTY_ADDRESS.toAddress()))
                        .getBalance();

        Assertions.assertEquals(U64.ZERO, balance);
    }

    @Test
    @Order(4)
    public void transfer() throws IOException {
        assetService.transfer(
                new TransferPayload(
                        asset_id.toHash(), EMPTY_ADDRESS.toAddress(), U64.fromLong(100)));

        U64 balance =
                assetService
                        .getBalance(
                                new GetBalancePayload(asset_id.toHash(), EMPTY_ADDRESS.toAddress()))
                        .getBalance();
        Assertions.assertEquals(U64.fromLong(100), balance);
    }

    @Test
    @Order(5)
    public void approve() throws IOException {
        GAddress another_account_address = backupAssetService.getMuta().getAccount().getAddress();

        assetService.approve(
                new TransferPayload(
                        asset_id.toHash(), another_account_address.toAddress(), U64.fromLong(200)));

        U64 allowance =
                assetService
                        .getAllowance(
                                new GetAllowancePayload(
                                        asset_id.toHash(),
                                        issuer.toAddress(),
                                        another_account_address.toAddress()))
                        .getValue();
        Assertions.assertEquals(U64.fromLong(200), allowance);

        log.debug("allowance: " + allowance);
        backupAssetService.transfer_from(
                new TransferFromPayload(
                        asset_id.toHash(),
                        issuer.toAddress(),
                        another_account_address.toAddress(),
                        U64.fromLong(50)));

        U64 balance =
                assetService
                        .getBalance(
                                new GetBalancePayload(
                                        asset_id.toHash(), another_account_address.toAddress()))
                        .getBalance();
        Assertions.assertEquals(U64.fromLong(50), balance);
    }
}
