package org.nervos.muta.test.wallet;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.nervos.muta.util.CryptoUtil;
import org.nervos.muta.util.Util;
import org.nervos.muta.wallet.Account;

public class AccountTest {

    @Test
    void testPrivateKey() {
        Account acc =
                Account.fromHexString(
                        "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");

        Assertions.assertEquals("0x8ed9a61dc092aaa7d7fd98ef710c9a0ce0e9cf08", acc.getAddressHex());
        Assertions.assertEquals(
                "muta13mv6v8wqj24204lanrhhzry6pnswnncga5c8cl", acc.getBech32Address());
        Assertions.assertEquals(
                "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f",
                acc.getPrivateKeyHex());
        Assertions.assertEquals(
                "0x031288a6788678c25952eba8693b2f278f66e2187004b64ac09416d07f83f96d5b",
                acc.getPublicKeyHex());
    }

    @Test
    void testRandomPrivateKey() {
        Account acc = Account.generate();

        Assertions.assertNotNull(acc.getAddressByteArray());
        Assertions.assertNotNull(acc.getPrivateKeyByteArray());
        Assertions.assertNotNull(acc.getPublicKeyByteArray());
    }

    @Test
    void testSign() {
        Account acc =
                Account.fromHexString(
                        "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");
        byte[] sig =
                acc.sign(
                        Hex.decode(
                                "0000000000000000000000000000000000000000000000000000000000000000"));
        Assertions.assertEquals(
                "09e9524859d589bff664a0c2284448229f156b946f8487f65c9f3476a6a0ed226b32326bb9bbbc181bbbf2ab90100634ddb5ba0553d340e0e9226a5fef301c43",
                Hex.toHexString(sig));
    }

    @Test
    void testVerify() {
        Account acc =
                Account.fromHexString(
                        "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");
        byte[] sig =
                acc.sign(
                        Hex.decode(
                                "0000000000000000000000000000000000000000000000000000000000000000"));

        Assertions.assertTrue(
                CryptoUtil.verify(
                        sig,
                        Hex.decode(
                                "0000000000000000000000000000000000000000000000000000000000000000"),
                        acc.getPublicKeyByteArray()));
    }

    @Test
    void testRecovery() {
        Account acc =
                Account.fromHexString(
                        "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");
        byte[] sig =
                acc.sign(
                        Hex.decode(
                                "0000000000000000000000000000000000000000000000000000000000000000"));
        Assertions.assertTrue(
                CryptoUtil.recovery(
                        sig,
                        Hex.decode(
                                "0000000000000000000000000000000000000000000000000000000000000000"),
                        acc.getAddressByteArray()));
    }

    @Test
    void testRandom() {
        int loop = 64;
        while (loop > 0) {
            loop--;
            Account acc = Account.generate();
            byte[] msgHash = Util.generateRandom32Bytes();
            byte[] sig = acc.sign(msgHash);
            Assertions.assertTrue(CryptoUtil.recovery(sig, msgHash, acc.getAddressByteArray()));
            Assertions.assertTrue(CryptoUtil.verify(sig, msgHash, acc.getPublicKeyByteArray()));
        }
    }

    @Test
    void testBech32FromToHex() {
        String hexAddress = "0x8ed9a61dc092aaa7d7fd98ef710c9a0ce0e9cf08";
        String bech32Address = "muta13mv6v8wqj24204lanrhhzry6pnswnncga5c8cl";

        String temp = Account.convertBech32AddressToHexAddress(bech32Address);
        Assertions.assertEquals(hexAddress, temp);

        byte[] tmp = Account.convertBech32AddressToBytesAddress(bech32Address);
        Assertions.assertArrayEquals(Hex.decode(Util.remove0x(hexAddress)), tmp);

        temp = Account.convertHexAddressToBech32Address(hexAddress);
        Assertions.assertEquals(bech32Address, temp);

        temp = Account.convertBytesAddressToBech32Address(Hex.decode(Util.remove0x(hexAddress)));
        Assertions.assertEquals(bech32Address, temp);
    }

    @Test
    void testBech32Validation() {
        boolean res =
                Account.validate_bech32_address(
                        "muta10e0525sfrf53yh2aljmm3sn9jq5njk7llt9agp", null);
        Assertions.assertTrue(res);
        res =
                Account.validate_bech32_address(
                        "muta10e0525sfrf53yh2aljmm3sn9jq5njk7llt9agp", "muta");
        Assertions.assertTrue(res);
        res = Account.validate_bech32_address("muta10e0525sfrf53yh2aljmm3sn9jq5njk7llt9agp", "xxx");
        Assertions.assertFalse(res);
    }
}
