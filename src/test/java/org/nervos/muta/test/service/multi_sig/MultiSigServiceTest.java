package org.nervos.muta.test.service.multi_sig;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.nervos.muta.Muta;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.client.type.graphql_schema.GHash;
import org.nervos.muta.client.type.graphql_schema.InputRawTransaction;
import org.nervos.muta.client.type.graphql_schema.InputTransactionEncryption;
import org.nervos.muta.client.type.primitive.U32;
import org.nervos.muta.client.type.primitive.U64;
import org.nervos.muta.client.type.primitive.U8;
import org.nervos.muta.service.asset.AssetService;
import org.nervos.muta.service.asset.type.Asset;
import org.nervos.muta.service.asset.type.CreateAssetPayload;
import org.nervos.muta.service.multi_sig.MultiSigService;
import org.nervos.muta.service.multi_sig.type.*;
import org.nervos.muta.wallet.Account;

@Slf4j
@Data
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MultiSigServiceTest {

    private static MultiSigService account1;
    private static MultiSigService account2;
    private static MultiSigService account3;
    private static GAddress account1addr;
    private static GAddress account2addr;
    private static GAddress account3addr;
    private static GAddress multi_sig_account;

    private static String asset_name = "MultiAsset";
    private static String asset_symbol = "MAS";
    private static U64 asset_supply = U64.fromLong(1000);

    public MultiSigServiceTest() {
        account1 =
                new MultiSigService(
                        new Muta(
                                Client.defaultClient(),
                                Account.fromHexString(
                                        "0x0000000000000000000000000000000000000000000000000000000000000001"),
                                null));
        account1addr = account1.getMuta().getAccount().getGAddress();
        log.debug(account1addr.toString());

        account2 =
                new MultiSigService(
                        new Muta(
                                Client.defaultClient(),
                                Account.fromHexString(
                                        "0x0000000000000000000000000000000000000000000000000000000000000002"),
                                null));
        account2addr = account2.getMuta().getAccount().getGAddress();
        log.debug(account2addr.toString());

        account3 =
                new MultiSigService(
                        new Muta(
                                Client.defaultClient(),
                                Account.fromHexString(
                                        "0x0000000000000000000000000000000000000000000000000000000000000003"),
                                null));
        account3addr = account3.getMuta().getAccount().getGAddress();
        log.debug(account3addr.toString());
    }

    @Test
    @Order(1)
    public void generate_account() throws IOException {
        GenerateMultiSigAccountResponse generateMultiSigAccountResponse =
                account1.generate_account(
                        new GenerateMultiSigAccountPayload(
                                account1addr.toAddress(),
                                false,
                                Arrays.asList(
                                        new AddressWithWeight(
                                                account1addr.toAddress(), U8.fromLong(5)),
                                        new AddressWithWeight(
                                                account2addr.toAddress(), U8.fromLong(5))),
                                U32.fromLong(10),
                                "test_multisig"));

        multi_sig_account = generateMultiSigAccountResponse.getAddress().toGAdress();
    }

    @Test
    @Order(2)
    public void get_account_from_address() throws IOException {
        GetMultiSigAccountResponse getMultiSigAccountResponse =
                account1.get_account_from_address(
                        new GetMultiSigAccountPayload(multi_sig_account.toAddress()));
        MultiSigPermission permission = getMultiSigAccountResponse.getPermission();
        Assertions.assertEquals(2, permission.getAccounts().size());
        Assertions.assertEquals(U8.fromLong(5), permission.getAccounts().get(0).getWeight());
        Assertions.assertEquals(U8.fromLong(5), permission.getAccounts().get(1).getWeight());
        Assertions.assertEquals(
                account1addr, permission.getAccounts().get(0).getAddress().toGAdress());
        Assertions.assertEquals(
                account2addr, permission.getAccounts().get(1).getAddress().toGAdress());
        Assertions.assertFalse(permission.getAccounts().get(0).is_multiple());
        Assertions.assertFalse(permission.getAccounts().get(1).is_multiple());
        Assertions.assertEquals(account1addr, permission.getOwner().toGAdress());
        Assertions.assertEquals(U32.fromLong(10), permission.getThreshold());
        Assertions.assertEquals("test_multisig", permission.getMemo());
    }

    @Test
    @Order(3)
    public void test_multisig_by_create_asset() throws IOException {
        InputRawTransaction inputRawTransaction =
                account1.getMuta()
                        .compose(
                                AssetService.SERVICE_NAME,
                                AssetService.METHOD_CREATE_ASSET,
                                new CreateAssetPayload(asset_name, asset_symbol, asset_supply),
                                multi_sig_account);
        InputTransactionEncryption inputTransactionEncryption =
                account1.getMuta().signTransaction(inputRawTransaction);

        inputTransactionEncryption =
                account2.getMuta()
                        .appendSignedTransaction(inputRawTransaction, inputTransactionEncryption);

        GHash txHash =
                account1.getMuta().sendTransaction(inputRawTransaction, inputTransactionEncryption);

        Asset asset =
                account1.getMuta()
                        .getReceiptSucceedDataRetry(
                                txHash, new TypeReference<Asset>() {}, new ArrayList<>())
                        .getSucceedData();
        Assertions.assertEquals(multi_sig_account, asset.getIssuer().toGAdress());
    }
}
