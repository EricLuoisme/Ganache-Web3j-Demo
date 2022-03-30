package com.example.ganacheweb3jdemo.web3j;

import okhttp3.OkHttpClient;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class WholeTest {

    // Eth-Rpc-Client
    private static final String LOCAL_GANACHE_NET_ADDRESS = "http://127.0.0.1:7545";

    // Credential Stuff
    private static final String ADDRESS_1 = "0x464CE7553EB107Cf1ae7a98aE4b17B0e13F8eecD";
    private static final String PRIKEY_1 = "a552672d7e80c6985fdbd6267b5b3d787955f0823d285964c92b07d6ea966d16";

    private static final String ADDRESS_2 = "0x2e5548061A57C3dA8De0382E479569d4A6de9B43";
    private static final String PRIKEY_2 = "03e5cee9c62655d0df6e4d5f9cc5fdf99e76914aa4b74f67e7aee551f8d7fe25";


    public static void main(String[] args) {

        Web3j web3 = Web3j.build(new HttpService(LOCAL_GANACHE_NET_ADDRESS));  // defaults to http://localhost:8545/

        try {
            Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println(clientVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
