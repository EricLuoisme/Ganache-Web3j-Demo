package com.example.web3j.combination.evm.polygon;

import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Doc:
 * https://docs.polygon.technology/docs/develop/network-details/network/
 * <p>
 * Open-Api:
 * https://edge-docs.polygon.technology/docs/get-started/json-rpc-commands/#eth_chainid
 * <p>
 * Testnet-Rpc:
 * https://rpc.poa.psdk.io:8545
 * https://rpc-mumbai.matic.today
 * https://matic-mumbai.chainstacklabs.com
 * https://rpc-mumbai.maticvigil.com
 * https://matic-testnet-archive-rpc.bwarelabs.com
 *
 * Mainnet-Api:
 * https://polygon.io/docs/stocks/getting-started
 *
 * @author Roylic
 * 2022/5/12
 */
public class PolygonMumbaiTest {

    private final static String POLYGON_MUMBAI_TESTNET_PATH = "https://rpc-mumbai.matic.today";

    private final static String WALLET_PATH = "0x70076F9f8e221d4729314f99a8AB410C117560aB";


    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    @Test
    public void simpleConnection() throws IOException {

        // Polygon Api 路径不变, 主要在于请求内容不同
        String connectData = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_chainId\",\"params\":[],\"id\":1}";
        String blockNumData = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":1}";
        String balanceData = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"" + WALLET_PATH + "\", \"latest\"],\"id\":1}";
        String txnCountData = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\":[\"" + WALLET_PATH + "\",\"latest\"],\"id\":1}";

        requestAndPrintf(txnCountData);
    }

    private void requestAndPrintf(String reqData) throws IOException {
        Request requestPost = new Request.Builder()
                .url(POLYGON_MUMBAI_TESTNET_PATH)
                .post(RequestBody.create(reqData, MediaType.parse("application/json")))
                .build();

        Call call = okHttpClient.newCall(requestPost);
        ResponseBody body = call.execute().body();
        if (null != body) {
            System.out.println(body.string());
        }
    }

}
