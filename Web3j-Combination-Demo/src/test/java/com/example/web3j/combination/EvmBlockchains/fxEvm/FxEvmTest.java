package com.example.web3j.combination.EvmBlockchains.fxEvm;

import com.example.web3j.combination.web3j.EthLogConstants;
import com.example.web3j.combination.web3j.handler.NftUriDecodeHandler;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    public void get721Log() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(8679536)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(8679536)),
                Collections.singletonList("0x9E4df6f08ceEcfEF170FCbF036B97789d5320ec3"));

        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_20_721));
        EthLog logs = web3j.ethGetLogs(filter).send();

        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;

            // filter target txn
            if (!"0x67909fba927f5171ba7f85c8d7acd247290c5497aa7038a2d1b0622784e9c567".equals(curLogObj.getTransactionHash())) {
                return;
            }

            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. from address
            String from = new Address(iterator.next()).getValue();

            // 3. to address
            String to = new Address(iterator.next()).getValue();

            // 4. tokenId
            String tokenStr = iterator.next();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(tokenStr, Utils.convert(EthLogConstants.EthFuncOutput.UINT256.getFuncOutputParams()));
            BigDecimal tokenId = new BigDecimal((BigInteger) valueDecodeList.get(0).getValue());

            System.out.println("From address " + from);
            System.out.println("To address " + to);
            System.out.println("Token id " + tokenId);
        });

    }


    @Test
    public void FoxContractNftDecoding() throws IOException {

        String contract = "0x9E4df6f08ceEcfEF170FCbF036B97789d5320ec3";
        String tokenIdStr = "105";

        String tokenBaseUri = NftUriDecodeHandler.tokenBaseUriRetrieving(web3j, contract, Collections.singletonList(tokenIdStr), NftUriDecodeHandler.SupportErc.ERC_721);
        System.out.println(tokenBaseUri);
    }


}
