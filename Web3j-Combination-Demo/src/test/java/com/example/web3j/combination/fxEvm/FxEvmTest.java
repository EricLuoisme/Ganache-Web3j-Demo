package com.example.web3j.combination.fxEvm;

import com.example.web3j.combination.web3j.handler.NftUriDecodeHandler;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;

/**
 * @author Roylic
 * 2022/11/7
 */
public class FxEvmTest {


    private static final String web3Url = "https://fx-json-web3.functionx.io:8545";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));


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
    public void getBlock() throws IOException {
        EthBlock block_5528640 = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("5528640")), false).send();
        System.out.println(block_5528640.toString());
    }

    @Test
    public void FoxContractNftDecoding() throws IOException {

        String contract = "0x9E4df6f08ceEcfEF170FCbF036B97789d5320ec3";
        String tokenIdStr = "105";

        String tokenBaseUri = NftUriDecodeHandler.tokenBaseUriRetrieving(web3j, contract, Collections.singletonList(tokenIdStr), NftUriDecodeHandler.SupportErc.ERC_721);
        System.out.println(tokenBaseUri);
    }


}
