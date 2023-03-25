package com.example.web3j.combination.EvmBlockchains.accounts;

import org.junit.jupiter.api.Test;
import org.web3j.crypto.*;

import java.io.File;
import java.io.IOException;

public class EvmAccountRelatedTest {

    @Test
    public void createBip39Wallet() throws CipherException, IOException {
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet("123456", new File("./"));
        System.out.println(bip39Wallet.getMnemonic());
    }

}
