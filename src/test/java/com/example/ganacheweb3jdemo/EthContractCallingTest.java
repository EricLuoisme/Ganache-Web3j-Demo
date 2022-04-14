package com.example.ganacheweb3jdemo;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
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
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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

        // ERC-20 获取该Token最小单位
        List<Type> decimalDecoded = callGetDecimal(erc20LogObj);
        Uint8 decimals = (Uint8) decimalDecoded.get(0);
        System.out.println(decimals.getValue());

        // ERC-20 获取Symbol (Unit)
        List<Type> symbolDecoded = callGetSymbol(erc20LogObj);
        Utf8String symbol = (Utf8String) symbolDecoded.get(0);
        System.out.println(symbol.getValue());
    }

    @Test
    public void callErc721Contract() throws IOException {

        EthLog.LogObject erc721LogObj = EthEventLogTest.getErc721LogObj();

        // ERC-721 获取Symbol (Unit)
        List<Type> symbolDecoded = callGetSymbol(erc721LogObj);
        Utf8String symbol = (Utf8String) symbolDecoded.get(0);
        System.out.println(symbol.getValue());

        // ERC-721 获取Name
        List<Type> nameDecoded = callGetName(erc721LogObj);
        Utf8String name = (Utf8String) nameDecoded.get(0);
        System.out.println(name.getValue());

        // ERC-721 获取token uri
        List<Type> tokenUriDecoded = callGetTokenUri(erc721LogObj);
        Utf8String tokenId = (Utf8String) tokenUriDecoded.get(0);
        System.out.println(tokenId.getValue());
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

    private List<Type> callGetTokenUri(EthLog.LogObject logObject) throws IOException {

        // decode and get token id
        List<Type> idDecode = FunctionReturnDecoder.decode(logObject.getTopics().get(3), Utils.convert(UINT256_OUTPUT));
        final Uint256 tokenId = (Uint256) idDecode.get(0);

        final Function tokenUriFunc = new Function(
                "tokenURI",
                Arrays.asList(tokenId),
                Collections.singletonList(TypeReference.create(Utf8String.class))
        );

        String encode = FunctionEncoder.encode(tokenUriFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(logObject.getAddress(), logObject.getAddress(), encode);

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        return FunctionReturnDecoder.decode(callResult.getValue(), tokenUriFunc.getOutputParameters());
    }
}
