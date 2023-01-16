package com.example.web3j.combination.node;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * @author Roylic
 * 2023/1/16
 */
public class ConnectionLoopTest {

    private static final String web3Url = "https://polygon-rpc.com";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));


    @Test
    public void testConnectionByCallingBalance() {

        int success = 0, failed = 0, total = 200;

        for (int i = 0; i < total; i++) {
            if (0 == i % 10) {
                System.out.println("Start calling: " + i);
            }
            Web3ClientVersion web3ClientVersion = null;
            try {
                EthGetBalance send = web3j.ethGetBalance("0xEd9dd2a4F4455C0B42b053343F74Af2F926537ae", DefaultBlockParameterName.LATEST).send();
                System.out.println(send.getBalance());
                success++;
            } catch (IOException e) {
                e.printStackTrace();
                failed++;
            }
        }

        System.out.println("Success Times: " + success);
        System.out.println("Failed Times: " + failed);
        System.out.println("Total Calling Times: " + total);
        System.out.println("Success Ratio: " + success / failed);
    }


}
