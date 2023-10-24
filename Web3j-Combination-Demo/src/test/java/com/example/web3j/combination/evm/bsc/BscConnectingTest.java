package com.example.web3j.combination.evm.bsc;

import com.example.web3j.combination.web3j.handler.NftUriDecodeHandler;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Collections;

/**
 * 链接bsc节点的测试
 *
 * @author Roylic
 * 2022/8/19
 */
public class BscConnectingTest {

    public static final Web3j web3j
//            = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));
//            = Web3j.build(new HttpService("http://testnet-bsc-dataseed1.functionx.io:8545/"));
            = Web3j.build(new HttpService("https://bsc-dataseed1.binance.org/"));

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

    @Test
    public void getNftTokenUri() {

        String contract = "0xCAd145eFC1eB20EB896b8F80e03D4d081Dd46D44";
        String tokenIdStr = "1";
        String tokenBaseUri = NftUriDecodeHandler.tokenBaseUriRetrieving(
                web3j, contract, Collections.singletonList(tokenIdStr), NftUriDecodeHandler.SupportErc.ERC_721);
        System.out.println(tokenBaseUri);
    }


}
