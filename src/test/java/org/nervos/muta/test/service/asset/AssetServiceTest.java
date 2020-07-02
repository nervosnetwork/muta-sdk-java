package org.nervos.muta.test.service.asset;

import java.io.IOException;
import java.math.BigInteger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.nervos.muta.Muta;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;
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
  private static Address EMPTY_ADDRESS =
      Address.fromHexString("0x0000000000000000000000000000000000000000");

  private static AssetService assetService;
  private static AssetService backupAssetService;

  private static Hash asset_id;

  private static Address issuer;

  private static Address another_account_address;

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
        assetService.createAsset(new CreateAssetPayload(ASSET_NAME, ASSET_SYMBOL, ASSET_SUPPLY));
    Assertions.assertEquals(ASSET_SUPPLY, asset.getSupply());
    Assertions.assertEquals(issuer, asset.getIssuer());
    Assertions.assertEquals(ASSET_SYMBOL, asset.getSymbol());
    asset_id = asset.getId();
    log.info("asset id: " + asset_id);
  }

  @Test
  @Order(2)
  public void getAsset() throws IOException {
    Asset asset = assetService.getAsset(new GetAssetPayload(asset_id));
    Assertions.assertEquals(ASSET_NAME, asset.getName());
  }

  @Test
  @Order(3)
  public void getBalance() throws IOException {
    U64 balance = assetService.getBalance(new GetBalancePayload(asset_id, issuer)).getBalance();

    Assertions.assertEquals(ASSET_SUPPLY, balance);

    balance = assetService.getBalance(new GetBalancePayload(asset_id, EMPTY_ADDRESS)).getBalance();

    Assertions.assertEquals(U64.ZERO, balance);
  }

  @Test
  @Order(4)
  public void transfer() throws IOException {
    assetService.transfer(new TransferPayload(asset_id, EMPTY_ADDRESS, U64.fromLong(100)));

    U64 balance =
        assetService.getBalance(new GetBalancePayload(asset_id, EMPTY_ADDRESS)).getBalance();
    Assertions.assertEquals(U64.fromLong(100), balance);
  }

  @Test
  @Order(5)
  public void approve() throws IOException {
    Address another_account_address = backupAssetService.getMuta().getAccount().getAddress();

    assetService.approve(new TransferPayload(asset_id, another_account_address, U64.fromLong(200)));

    U64 allowance =
        assetService
            .getAllowance(new GetAllowancePayload(asset_id, issuer, another_account_address))
            .getValue();
    Assertions.assertEquals(U64.fromLong(200), allowance);

    log.debug("allowance: " + allowance);
    backupAssetService.transfer_from(
        new TransferFromPayload(asset_id, issuer, another_account_address, U64.fromLong(50)));

    U64 balance =
        assetService
            .getBalance(new GetBalancePayload(asset_id, another_account_address))
            .getBalance();
    Assertions.assertEquals(U64.fromLong(50), balance);
  }
}
