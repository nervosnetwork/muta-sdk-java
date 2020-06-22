package org.nervos.muta.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bitcoinj.crypto.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class Wallet {

    public static String HD_PATH = "44H/918H/";
    public static String HD_PATH_CHARGE_INDEX = "H/0/0";

    private final byte[]  seed;
    private final DeterministicKey master_node;

    public static Wallet from_mnemonic(List<String> words, String passphrase){
        byte[] seed = MnemonicCode.toSeed(words,passphrase);
        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
        return new Wallet(seed,master);
    }

    public static Wallet from_mnemonic(List<String> words){
        return Wallet.from_mnemonic(words,"");
    }

    public static Wallet from_mnemonic(String wordSentence){
        List<String> words = Arrays.asList(wordSentence.split(" "));
        return Wallet.from_mnemonic(words,"");
    }


    public Account derive(int account_index){
        String path = HD_PATH + account_index + HD_PATH_CHARGE_INDEX;
        List<ChildNumber> childNumbers = HDUtils.parsePath(path);
        DeterministicKey node= this.master_node;
        for (ChildNumber cn : childNumbers) {
            node = HDKeyDerivation.deriveChildKey(node, cn);
        }

        if(node == null){
            throw new RuntimeException("derive error");
        }
        String privateKeyHex = node.getPrivateKeyAsHex();

        return new Account(privateKeyHex);
    }
 }
