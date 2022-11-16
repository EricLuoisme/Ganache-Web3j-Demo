package com.example.web3j.combination.eth;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Roylic
 * 2022/11/16
 */
public class GoerliContractCallingTest {

    public static final String INFURA_GOERLI_NODE_HTTP_LINK
            = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j
            = Web3j.build(new HttpService(INFURA_GOERLI_NODE_HTTP_LINK));


    @Test
    public void decode1155Single() throws IOException {

        long blockHeight = 7962780L;

        // filter for event
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(blockHeight)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(blockHeight)),
                Collections.emptyList());

        Event transferSingle = new Event("TransferSingle",
                Arrays.asList(
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Uint256.class),
                        TypeReference.create(Uint256.class))
        );

        // add into filter
        String topicStr = EventEncoder.encode(transferSingle);
        filter.addOptionalTopics(topicStr);

        // decode
        EthLog ethLogResp = web3j.ethGetLogs(filter).send();
        ethLogResp.getLogs().forEach(ethLog -> {
            EthLog.LogObject logObject = (EthLog.LogObject) ethLog;
            List<TypeReference<Type>> nonIndexedParameters = transferSingle.getNonIndexedParameters();
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
            System.out.println("raw url -> " + url);
            url.replaceAll("\\{id}", tokenId.getValue().toString());
            System.out.println("url -> " + url);
            System.out.println();
        });


    }


    @Test
    public void decode1155Batch() throws IOException {

        long blockHeight = 7962506L;
        String contract = "0x87b5b583732eb0d6876221b0e6a8e95afddb8f68";

        // filter for event
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(blockHeight)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(blockHeight)),
                Collections.emptyList());

        // focus on ERC-1155's transferBatch event
        Event transferBatch = new Event("TransferBatch",
                Arrays.asList(
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        new TypeReference<DynamicArray<Uint256>>() {
                        },
                        new TypeReference<DynamicArray<Uint256>>() {
                        })
        );

        // add into filter
        String topicStr = EventEncoder.encode(transferBatch);
        filter.addOptionalTopics(topicStr);

        // decode
        EthLog ethLogResp = web3j.ethGetLogs(filter).send();
        ethLogResp.getLogs().forEach(ethLog -> {
            EthLog.LogObject logObject = (EthLog.LogObject) ethLog;
            List<TypeReference<Type>> nonIndexedParameters = transferBatch.getNonIndexedParameters();
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
        });


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
