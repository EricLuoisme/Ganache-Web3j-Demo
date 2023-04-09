package com.example.web3j.combination.EvmBlockchains.eth;

import com.example.web3j.combination.web3j.EthLogConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
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
        String txHash = "0x6b373df3d9b7e6ce097dfe32a5fb4f4a9324fe3142eb9c7d9e444213d215f07f";
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


    @Test
    public void decodeErc1155TokenUri() throws IOException {
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(7864400L)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(7864400L)),
                Collections.emptyList());

        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_1155_SINGLE));
        EthLog logs = web3j.ethGetLogs(filter).send();
        logs.getLogs().forEach(sinEthLog -> System.out.println(callGetErc1155SingleUri((EthLog.LogObject) sinEthLog)));

    }

    private String callGetErc1155SingleUri(EthLog.LogObject logObject) {
        // decode token id
        List<TypeReference<Type>> nonIndexedParameters = EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_1155_SINGLE.event.getNonIndexedParameters();
        List<Type> decode = FunctionReturnDecoder.decode(logObject.getData(), nonIndexedParameters);
        Uint256 tokenId = (Uint256) decode.get(0);

        Function uriFunc = new Function(
                "uri",
                Collections.singletonList(tokenId),
                Collections.singletonList(TypeReference.create(Utf8String.class))
        );
        String encode = FunctionEncoder.encode(uriFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(logObject.getAddress(), logObject.getAddress(), encode);
        EthCall callResult = null;
        try {
            callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // decode uri
        List<Type> uriDecoded = FunctionReturnDecoder.decode(callResult.getValue(), uriFunc.getOutputParameters());
        Utf8String uri = (Utf8String) uriDecoded.get(0);

        // replace {id} with real token id
        String url = uri.getValue();
        return url.replaceAll("\\{id}", tokenId.getValue().toString());
    }


    private List<String> callGetErc1155BatchUri(EthLog.LogObject logObject) {
        // decode and get token ids (no need for amounts)
        List<TypeReference<Type>> nonIndexedParameters = EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_1155_BATCH.event.getNonIndexedParameters();
        List<Type> decodeArr = FunctionReturnDecoder.decode(logObject.getData(), nonIndexedParameters);

        DynamicArray<Uint256> tokenIdDyArr = (DynamicArray<Uint256>) decodeArr.get(0);
        List<Uint256> tokenId = tokenIdDyArr.getValue();

        List<String> urlList = Lists.newArrayList();

        tokenId.stream().forEach(id -> {
            try {
                String realUrl = callOnce(id, logObject.getAddress());
                urlList.add(realUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return urlList;
    }

    private String callOnce(Uint256 tokenId, String contractAddress) throws IOException {
        Function uriFunc = new Function(
                "uri",
                Collections.singletonList(tokenId),
                Collections.singletonList(TypeReference.create(Utf8String.class))
        );
        String encode = FunctionEncoder.encode(uriFunc);

        System.out.println("ERC-1155-Code>>>" + encode);

        Transaction reqTxn = Transaction.createEthCallTransaction(contractAddress, contractAddress, encode);
        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        List<Type> uriDecoded = FunctionReturnDecoder.decode(callResult.getValue(), uriFunc.getOutputParameters());
        Utf8String uri = (Utf8String) uriDecoded.get(0);

        // replace {id} with real token id
        String url = uri.getValue();
        System.out.println();
        System.out.println(">>> Raw Url >>> " + url);

        return url.replaceAll("\\{id}", tokenId.getValue().toString());
    }

}
