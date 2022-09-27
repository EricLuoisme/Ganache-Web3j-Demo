package com.example.web3j.combination.ethw;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * EthW
 *
 * @author Roylic
 * 2022/9/23
 */
public class EthWTest {


    private static final String web3Url = "https://iceberg.ethereumpow.org";
    private static final String local_web3Url = "http://127.0.0.1:8545";
    private static final String local_wss = "ws://127.0.0.1:8551";

    public static final Web3j web3j = Web3j.build(new HttpService(local_web3Url));

    private static final String address = "0xe10eE98bB84B2073B88353e3AB4433916205DF40";

    @Test
    public void testConnection() {
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println();
            System.out.println(clientVersion);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlock() {

    }



    @Test
    public void checkBalance() throws IOException {

        EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        System.out.println(balance.getBalance());

    }


}
