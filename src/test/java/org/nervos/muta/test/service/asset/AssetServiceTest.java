package org.nervos.muta.test.service.asset;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.nervos.muta.Muta;
import org.nervos.muta.client.Client;
import org.nervos.muta.service.asset.AssetService;
import org.nervos.muta.service.asset.type.Asset;
import org.nervos.muta.wallet.Account;

import java.io.IOException;

@Slf4j
@Data
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AssetServiceTest {

    private static String ASSET_NAME = "Hamster Coin";
    private static String ASSET_SYMBOL = "HAM";
    private static long ASSET_SUPPLY = 1000;
    private static String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";

    private static AssetService assetService;
    private static AssetService backupAssetService;

    private static String asset_id;

    private static String issuer;

    private static String another_account_address;

    public AssetServiceTest() {
        assetService = new AssetService(
                Muta.defaultMuta()
        );

        backupAssetService = new AssetService(
                new Muta(
                        Client.defaultClient(),
                        Account.generate(),
                        null
                )
        );

        issuer = assetService.getMuta().getAccount().getAddress();
        another_account_address = backupAssetService.getMuta().getAccount().getAddress();
    }

    @Test
    @Order(1)
    public void createAsset() throws IOException {
        Asset asset = assetService.createAsset(ASSET_NAME, ASSET_SYMBOL, ASSET_SUPPLY);
        Assertions.assertEquals(ASSET_SUPPLY, asset.getSupply());
        Assertions.assertEquals(issuer, asset.getIssuer());
        Assertions.assertEquals(ASSET_SYMBOL, asset.getSymbol());
        asset_id = asset.getId();
        log.info("asset id: " + asset_id);
    }

    @Test
    @Order(2)
    public void getAsset() throws IOException {
        Asset asset = assetService.getAsset(asset_id);
        Assertions.assertEquals(ASSET_NAME, asset.getName());
    }

    @Test
    @Order(3)
    public void getBalance() throws IOException {
        long balance = assetService.getBalance(asset_id, issuer).getBalance();

        Assertions.assertEquals(ASSET_SUPPLY, balance);

        balance = assetService.getBalance(asset_id, EMPTY_ADDRESS).getBalance();

        Assertions.assertEquals(0, balance);

    }

    @Test
    @Order(4)
    public void transfer() throws IOException {
        assetService.transfer(asset_id, EMPTY_ADDRESS, 100);

        long balance = assetService.getBalance(asset_id, EMPTY_ADDRESS).getBalance();
        Assertions.assertEquals(100, balance);
    }

    @Test
    @Order(5)
    public void approve() throws IOException {
        String another_account_address = backupAssetService.getMuta().getAccount().getAddress();

        assetService.approve(asset_id, another_account_address, 200);

        long allowance = assetService.getAllowance(asset_id, issuer, another_account_address).getValue();
        Assertions.assertEquals(200, allowance);

        log.debug("allowance: " + allowance);
        backupAssetService.transfer_from(
                asset_id,
                issuer,
                another_account_address,
                50
        );

        long balance = assetService.getBalance(asset_id, another_account_address).getBalance();
        Assertions.assertEquals(50, balance);
    }
}
