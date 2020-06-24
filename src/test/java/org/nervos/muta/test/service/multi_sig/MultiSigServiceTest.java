package org.nervos.muta.test.service.multi_sig;

import lombok.Data;
import org.junit.jupiter.api.*;
import org.nervos.muta.Muta;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.request.RawTransaction;
import org.nervos.muta.client.type.request.TransactionEncryption;
import org.nervos.muta.service.asset.AssetService;
import org.nervos.muta.service.asset.type.Asset;
import org.nervos.muta.service.asset.type.CreateAssetPayload;
import org.nervos.muta.service.multi_sig.MultiSigService;
import org.nervos.muta.service.multi_sig.type.AddressWithWeight;
import org.nervos.muta.service.multi_sig.type.ChangeOwnerPayload;
import org.nervos.muta.service.multi_sig.type.GenerateMultiSigAccountResponse;
import org.nervos.muta.service.multi_sig.type.GetMultiSigAccountResponse;
import org.nervos.muta.wallet.Account;

import java.io.IOException;
import java.util.Arrays;

@Data
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MultiSigServiceTest {

    private static MultiSigService account1;
    private static MultiSigService account2;
    private static MultiSigService account3;
    private static String account1addr;
    private static String account2addr;
    private static String account3addr;
    private static String multi_sig_account;

    private static String asset_name = "MultiAsset";
    private static String asset_symbol = "MAS";
    private static long asset_supply = 1000;

    public MultiSigServiceTest() {
        account1 = new MultiSigService(new Muta(
                Client.defaultClient(),
                new Account("0x0000000000000000000000000000000000000000000000000000000000000001"),
                null
        ));
        account1addr = account1.getMuta().getAccount().getAddress();
        System.out.println(account1addr);

        account2 = new MultiSigService(new Muta(
                Client.defaultClient(),
                new Account("0x0000000000000000000000000000000000000000000000000000000000000002"),
                null
        ));
        account2addr = account2.getMuta().getAccount().getAddress();
        System.out.println(account2addr);

        account3 = new MultiSigService(new Muta(
                Client.defaultClient(),
                new Account("0x0000000000000000000000000000000000000000000000000000000000000003"),
                null
        ));
        account3addr = account3.getMuta().getAccount().getAddress();
        System.out.println(account3addr);

    }

    @Test
    @Order(1)
    public void generate_account() throws IOException {
        GenerateMultiSigAccountResponse generateMultiSigAccountResponse = account1.generate_account(
                account1addr, Arrays.asList(
                        new AddressWithWeight(account1addr, 5),
                        new AddressWithWeight(account2addr, 5)
                ), 10, "test_multisig"
        );

        multi_sig_account = generateMultiSigAccountResponse.getAddress();
    }

    @Test
    @Order(2)
    public void get_account_from_address() throws IOException {
        GetMultiSigAccountResponse getMultiSigAccountResponse = account1.get_account_from_address(multi_sig_account);
        GetMultiSigAccountResponse.MultiSigPermission permission = getMultiSigAccountResponse.getPermission();
        Assertions.assertEquals(2, permission.getAccounts().size());
        Assertions.assertEquals(5, permission.getAccounts().get(0).getWeight());
        Assertions.assertEquals(5, permission.getAccounts().get(1).getWeight());
        Assertions.assertEquals(account1addr, permission.getAccounts().get(0).getAddress());
        Assertions.assertEquals(account2addr, permission.getAccounts().get(1).getAddress());
        Assertions.assertEquals(false, permission.getAccounts().get(0).is_multiple());
        Assertions.assertEquals(false, permission.getAccounts().get(1).is_multiple());
        Assertions.assertEquals(account1addr, permission.getOwner());
        Assertions.assertEquals(10, permission.getThreshold());
        Assertions.assertEquals("test_multisig", permission.getMemo());

    }

    @Test
    @Order(3)
    public void test_multisig_by_create_asset() throws IOException {
        RawTransaction rawTransaction = account1.getMuta().compose(AssetService.SERVICE_NAME, AssetService.METHOD_CREATE_ASSET,
                new CreateAssetPayload(
                        asset_name,
                        asset_symbol,
                        asset_supply),
                multi_sig_account
        );
        TransactionEncryption transactionEncryption = account1.getMuta().signTransaction(rawTransaction);

        transactionEncryption = account2.getMuta().appendSignedTransaction(rawTransaction, transactionEncryption);

        String txHash = account1.getMuta().sendTransaction(rawTransaction, transactionEncryption);

        Asset asset = account1.getMuta().getReceiptSucceedDataRetry(txHash, Asset.class);
        Assertions.assertEquals(multi_sig_account, asset.getIssuer());
    }

}
