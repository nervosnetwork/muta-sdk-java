package org.nervos.muta.wallet;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.util.Util;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

/**
 * Wallet handles mnemonic and derive job.
 *
 * @author Lycrus Hamster
 */
@Data
@AllArgsConstructor
public class Wallet {
    private final byte[] seed;

    public static Wallet from_mnemonic(String mnemonic, String passphrase) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, passphrase);
        return new Wallet(seed);
    }

    public static Wallet from_mnemonic(List<String> words) {
        return Wallet.from_mnemonic(String.join(" ", words), "");
    }

    public static Wallet from_mnemonic(String words) {
        return Wallet.from_mnemonic(words, "");
    }

    public Account derive(int coin_type, int account_index) {

        Bip32ECKeyPair pair = Bip32ECKeyPair.generateKeyPair(this.seed);

        // m/44'/918'/0'/0/0
        // m / purpose' / coin_type' / account' / change / address_index
        final int[] path = {
            44 | HARDENED_BIT, coin_type | HARDENED_BIT, account_index | HARDENED_BIT, 0, 0
        };

        Bip32ECKeyPair acc = Bip32ECKeyPair.deriveKeyPair(pair, path);
        byte[] priv = Util.bigIntegerToBytes32(acc.getPrivateKey());

        return Account.fromByteArray(priv);
    }
}
