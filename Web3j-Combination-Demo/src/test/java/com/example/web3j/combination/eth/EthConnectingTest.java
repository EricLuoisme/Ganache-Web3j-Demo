package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * 链接bsc节点的测试
 *
 * @author Roylic
 * 2022/8/19
 */
public class EthConnectingTest {

    public static final Web3j web3j
            = Web3j.build(new HttpService("https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6"));
//            = Web3j.build(new HttpService("https://eth-kovan.alchemyapi.io/v2/IyfMvMw8kE7X3KvFOwoTlAvDB2QaEVoD"));

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
    public void getLatestBlock() throws IOException {
        EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                .send().getBlock();
        System.out.println(block.getNumber().toString());
    }


}
