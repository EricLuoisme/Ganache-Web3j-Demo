package com.example.web3j.combination.evm.base;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * @author Roylic
 * 2023/10/9
 */
public class BaseGoerliTests {


    //    public static final Web3j web3j = Web3j.build(new HttpService("https://mainnet.base.org"));
    public static final Web3j web3j = Web3j.build(new HttpService("https://goerli.base.org"));

    @Test
    public void testConnection() {
        try {
            EthChainId chainIdResp = web3j.ethChainId().send();
            System.out.println(chainIdResp.getChainId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
