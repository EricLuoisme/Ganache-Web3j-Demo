package com.example.ganacheweb3jdemo;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Roylic
 * @date 2022/4/14
 */
public class EthContractCallingTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK
            = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";

    public static final Web3j web3j
            = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));

    public static final Web3j web3j_functionx
            = Web3j.build(new HttpService("http://testnet-bsc-dataseed2.functionx.io:8545"));

    public static final List<TypeReference<?>> UINT256_OUTPUT
            = Stream.of(TypeReference.create(Uint256.class)).collect(Collectors.toList());

    /**
     * ERC-20 + ERC-721
     */
    public static final Function symbolFunc = new Function(
            "symbol",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Utf8String.class))
    );

    /**
     * ERC-20
     */
    public static final Function decimalFunc = new Function(
            "decimals",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Uint8.class)));

    /**
     * ERC-721
     */
    public static final Function nameFunc = new Function(
            "name",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Utf8String.class)));


    @Test
    public void callErc20Contract() throws IOException {
        EthLog.LogObject erc20LogObj = EthEventLogTest.getErc20LogObj();

        // ERC-20 获取Name
        List<Type> nameDecoded = callGetName(erc20LogObj);
        Utf8String name = (Utf8String) nameDecoded.get(0);
        System.out.println(name.getValue());

        // ERC-20 获取Symbol (Unit)
        List<Type> symbolDecoded = callGetSymbol(erc20LogObj);
        Utf8String symbol = (Utf8String) symbolDecoded.get(0);
        System.out.println(symbol.getValue());

        // ERC-20 获取该Token最小单位
        List<Type> decimalDecoded = callGetDecimal(erc20LogObj);
        Uint8 decimals = (Uint8) decimalDecoded.get(0);
        System.out.println(decimals.getValue());
    }

    @Test
    public void callErc721Contract() throws IOException {
        EthLog.LogObject erc721LogObj = EthEventLogTest.getErc721LogObj();

        // ERC-721 获取Name
        List<Type> nameDecoded = callGetName(erc721LogObj);
        Utf8String name = (Utf8String) nameDecoded.get(0);
        System.out.println(name.getValue());

        // ERC-721 获取Symbol (Unit)
        List<Type> symbolDecoded = callGetSymbol(erc721LogObj);
        Utf8String symbol = (Utf8String) symbolDecoded.get(0);
        System.out.println(symbol.getValue());

        // ERC-721 获取token uri
        List<Type> tokenUriDecoded = callGetErc721TokenUri(erc721LogObj);
        Utf8String tokenId = (Utf8String) tokenUriDecoded.get(0);
        System.out.println(tokenId.getValue());
    }

    @Test
    public void callErc1155Contract() throws IOException {
        // ERC-1155 Single 获取uri
        EthLog.LogObject erc1155SingleLogObj = EthEventLogTest.getErc1155SingleLogObj();
        System.out.println();
        System.out.println("ERC-1155 Single token url >>> " + callGetErc1155SingleUri(erc1155SingleLogObj));

        // ERC-1155 Batch 获取uri
//        EthLog.LogObject erc1155BatchLogObj = EthEventLogTest.getErc1155BatchLogObj();
//        List<String> urlList = callGetErc1155BatchUri(erc1155BatchLogObj);
//        urlList.forEach(System.out::println);
    }


    private List<Type> callGetDecimal(EthLog.LogObject logObject) throws IOException {

        String encodedDecimalFunc = FunctionEncoder.encode(decimalFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(
                logObject.getAddress(),
                logObject.getAddress(),
                encodedDecimalFunc);

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        return FunctionReturnDecoder.decode(callResult.getValue(), decimalFunc.getOutputParameters());
    }

    private List<Type> callGetSymbol(EthLog.LogObject logObject) throws IOException {

        String encodedSymbolFunc = FunctionEncoder.encode(symbolFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(
                logObject.getAddress(),
                logObject.getAddress(),
                encodedSymbolFunc
        );

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        return FunctionReturnDecoder.decode(callResult.getValue(), symbolFunc.getOutputParameters());
    }

    private List<Type> callGetName(EthLog.LogObject logObject) throws IOException {

        String encode = FunctionEncoder.encode(nameFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(
                logObject.getAddress(),
                logObject.getAddress(),
                encode
        );

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        return FunctionReturnDecoder.decode(callResult.getValue(), nameFunc.getOutputParameters());
    }

    private List<Type> callGetErc721TokenUri(EthLog.LogObject logObject) throws IOException {
        // decode and get token id
        List<Type> idDecode = FunctionReturnDecoder.decode(logObject.getTopics().get(3), Utils.convert(UINT256_OUTPUT));
        final Uint256 tokenId = (Uint256) idDecode.get(0);

        final Function tokenUriFunc = new Function(
                "tokenURI",
                Collections.singletonList(tokenId),
                Collections.singletonList(TypeReference.create(Utf8String.class))
        );

        String encode = FunctionEncoder.encode(tokenUriFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(logObject.getAddress(), logObject.getAddress(), encode);

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        return FunctionReturnDecoder.decode(callResult.getValue(), tokenUriFunc.getOutputParameters());
    }

    private String callGetErc1155SingleUri(EthLog.LogObject logObject) throws IOException {
        // decode token id
        List<TypeReference<Type>> nonIndexedParameters = EthEventTopics.TRANSFER_TOPIC_ERC_1155_SINGLE.event.getNonIndexedParameters();
        List<Type> decode = FunctionReturnDecoder.decode(logObject.getData(), nonIndexedParameters);
        Uint256 tokenId = (Uint256) decode.get(0);

        Function uriFunc = new Function(
                "uri",
                Collections.singletonList(tokenId),
                Collections.singletonList(TypeReference.create(Utf8String.class))
        );
        String encode = FunctionEncoder.encode(uriFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(logObject.getAddress(), logObject.getAddress(), encode);
        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        // decode uri
        List<Type> uriDecoded = FunctionReturnDecoder.decode(callResult.getValue(), uriFunc.getOutputParameters());
        Utf8String uri = (Utf8String) uriDecoded.get(0);

        // replace {id} with real token id
        String url = uri.getValue();
        return url.replaceAll("\\{id}", tokenId.getValue().toString());
    }

    private List<String> callGetErc1155BatchUri(EthLog.LogObject logObject) throws IOException {
        // decode and get token ids (no need for amounts)
        List<TypeReference<Type>> nonIndexedParameters = EthEventTopics.TRANSFER_TOPIC_ERC_1155_BATCH.event.getNonIndexedParameters();
        List<Type> decodeArr = FunctionReturnDecoder.decode(logObject.getData(), nonIndexedParameters);

        DynamicArray<Uint256> tokenIdDyArr = (DynamicArray<Uint256>) decodeArr.get(0);
        List<Uint256> tokenId = tokenIdDyArr.getValue();

        List<String> urlList = Collections.emptyList();

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

        Transaction reqTxn = Transaction.createEthCallTransaction(contractAddress, contractAddress, encode);
        EthCall callResult = web3j_functionx.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        List<Type> uriDecoded = FunctionReturnDecoder.decode(callResult.getValue(), uriFunc.getOutputParameters());
        Utf8String uri = (Utf8String) uriDecoded.get(0);

        // replace {id} with real token id
        String url = uri.getValue();
        return url.replaceAll("\\{id}", tokenId.getValue().toString());
    }


}
