package org.nervos.muta.test.wallet;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.nervos.muta.wallet.Account;
import org.nervos.muta.wallet.Wallet;

public class WalletTest {

    @Test
    public void testMnemonic() {
        Wallet wallet =
                Wallet.from_mnemonic(
                        "drastic behave exhaust enough tube judge real logic escape critic horror gold");
        Assertions.assertEquals(
                "85e0ed4f4f6fe7d9ceef310d7e21420b8ef3b359feaf3a76d41473d42074f453d6e2222a217bf9d9c2b04f4c533236495fe1bfd4b936612585ccd3b72ae82750",
                Hex.toHexString(wallet.getSeed()));
    }

    @Test
    public void testDerive() {
        Wallet wallet =
                Wallet.from_mnemonic(
                        "drastic behave exhaust enough tube judge real logic escape critic horror gold");
        Account account = wallet.derive(918, 0);
        // 85e0ed4f4f6fe7d9ceef310d7e21420b8ef3b359feaf3a76d41473d42074f453d6e2222a217bf9d9c2b04f4c533236495fe1bfd4b936612585ccd3b72ae82750
        Assertions.assertEquals(
                "0x8ceac4c591bfb13c6cc4b211f83df53f1edd54a88970f1ee88eeda8d07c0e161",
                account.getPrivateKeyHex());
        Assertions.assertEquals(
                "0x02390eef68b9a39c6c08b5bfaed0efb8f1654746b55c35ff616374b4522a0ab01a",
                account.getPublicKeyHex());
    }
}
