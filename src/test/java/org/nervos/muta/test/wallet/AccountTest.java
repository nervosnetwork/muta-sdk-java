package org.nervos.muta.test.wallet;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.nervos.muta.wallet.Account;

public class AccountTest {

    @Test
    void testPrivateKey() {
        Account acc =
                Account.fromHexString(
                        "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");

        Assertions.assertEquals("0xf8389d774afdad8755ef8e629e5a154fddc6325a", acc.getAddressHex());
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
}
