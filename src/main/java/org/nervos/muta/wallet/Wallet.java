package org.nervos.muta.wallet;

import lombok.AllArgsConstructor;
import org.bitcoinj.crypto.*;

import java.util.List;

@AllArgsConstructor
public class Wallet {

    public static String HD_PATH = "44H/918H/";
    public static String HD_PATH_CHARGE_INDEX = "H/0/0";
    public DeterministicKey master_node;


    public static Wallet from_mnemonic(List<String> words, String passphrase){
        byte[] seed = MnemonicCode.toSeed(words,passphrase);
        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
        return new Wallet(master);
    }

    public Account derive(int account_index){
        String path = HD_PATH + account_index + HD_PATH_CHARGE_INDEX;
        List<ChildNumber> childNumbers = HDUtils.parsePath(path);
        DeterministicKey temp = null;
        for (ChildNumber cn : childNumbers) {
            temp = HDKeyDerivation.deriveChildKey(this.master_node, cn);
        }


        if(temp == null || temp.hasPrivKey()){

        }

        String privateKeyHex = temp.getPrivateKeyAsHex();

        return new Account(privateKeyHex);
    }
 }
