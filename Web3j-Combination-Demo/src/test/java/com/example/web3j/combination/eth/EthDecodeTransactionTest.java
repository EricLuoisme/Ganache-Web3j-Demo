package com.example.web3j.combination.eth;

import com.example.web3j.combination.web3j.EthLogConstants;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roylic
 * 2022/10/31
 */
public class EthDecodeTransactionTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK
            = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j
            = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));


    @Test
    public void decodeTxn() throws IOException {
        // deploy contract / interact with contract -> input != 0x
//        String txHash = "0x066665f26ebab1a4eff812c245e2308b6554ca44114c5e0232367a52686c9d4a";

        // eth transfer -> input == 0x
        String txHash = "0x7faba547af0b637cf0890486ab41d23ae048b6d6bea149eeb818fd4bddbfcc53";
        EthTransaction txn = web3j.ethGetTransactionByHash(txHash).send();
        System.out.println(txn);
    }

    @Test
    public void decodeErc20() throws IOException {
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(7865157L)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(7865157L)),
                Collections.emptyList());

        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_20_721));


        EthLog logs = web3j.ethGetLogs(filter).send();
        logs.getLogs().forEach(sinEthLog -> {
            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. from address
            String from = new Address(iterator.next()).getValue();

            // 3. to address
            String to = new Address(iterator.next()).getValue();

            // 4. amount
            String valueStr = curLogObj.getData();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, Utils.convert(EthLogConstants.EthFuncOutput.UINT256.getFuncOutputParams()));
            BigDecimal amount = new BigDecimal((BigInteger) valueDecodeList.get(0).getValue());

            System.out.println();
        });
    }

    @Test
    public void decodeErc721() throws IOException {
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(226747L)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(226747L)),
                Collections.emptyList());

        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_20_721));


        EthLog logs = web3j.ethGetLogs(filter).send();
        logs.getLogs().forEach(sinEthLog -> {
            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. from address
            String from = new Address(iterator.next()).getValue();

            // 3. to address
            String to = new Address(iterator.next()).getValue();

            // 4. amount (erc-721 only supported transfer single token each time)
            String valueStr = iterator.next();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, Utils.convert(EthLogConstants.EthFuncOutput.UINT256.getFuncOutputParams()));
            BigDecimal amount = BigDecimal.ONE;

            System.out.println();
        });
    }

}
