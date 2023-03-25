package com.example.web3j.combination.EvmBlockchains.eth;

import com.example.web3j.combination.web3j.EthLogConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
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
            = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

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
        System.out.println("ERC-20 token name >>> " + name.getValue());

        // ERC-20 获取Symbol (Unit)
        List<Type> symbolDecoded = callGetSymbol(erc20LogObj);
        Utf8String symbol = (Utf8String) symbolDecoded.get(0);
        System.out.println("ERC-20 token symbol >>> " + symbol.getValue());

        // ERC-20 获取该Token最小单位
        List<Type> decimalDecoded = callGetDecimal(erc20LogObj);
        Uint8 decimals = (Uint8) decimalDecoded.get(0);
        System.out.println("ERC-29 token decimal >>> " + decimals.getValue());

        System.out.println("ERC-20 interface supports >>> " + checkErc165Support(erc20LogObj));
    }

    @Test
    public void callErc721Contract() throws IOException {
        EthLog.LogObject erc721LogObj = EthEventLogTest.getErc721LogObj();

        // ERC-721 获取Name
        List<Type> nameDecoded = callGetName(erc721LogObj);
        Utf8String name = (Utf8String) nameDecoded.get(0);
        System.out.println("ERC-721 token name >>> " + name.getValue());

        // ERC-721 获取Symbol (Unit)
        List<Type> symbolDecoded = callGetSymbol(erc721LogObj);
        Utf8String symbol = (Utf8String) symbolDecoded.get(0);
        System.out.println("ERC-721 token symbol >>> " + symbol.getValue());

        // ERC-721 获取token uri
        List<Type> tokenUriDecoded = callGetErc721TokenUri(erc721LogObj);
        Utf8String tokenId = (Utf8String) tokenUriDecoded.get(0);
        System.out.println("ERC-721 token uri >>> " + tokenId.getValue());

        System.out.println("ERC-721 interface supports >>> " + checkErc165Support(erc721LogObj));
    }

    @Test
    public void callErc1155Contract() throws IOException {
//        // ERC-1155 Single 获取uri
//        EthLog.LogObject erc1155SingleLogObj = EthEventLogTest.getErc1155SingleLogObj();
//        System.out.println();
//        System.out.println("ERC-1155 Single token url >>> " + callGetErc1155SingleUri(erc1155SingleLogObj));

        // ERC-1155 Batch 获取uri
        List<EthLog.LogObject> erc1155BatchList = EthEventLogTest.getErc1155BatchLogObjectList();
        erc1155BatchList.forEach(logObject -> {
            try {
                List<String> urlList = callGetErc1155BatchUri(logObject);
                System.out.println("For New Log Object >>> ");
                urlList.forEach(str -> {
                    System.out.println("  Uri >>> " + str);
                });
                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


//        EthLog.LogObject erc1155BatchLogObj = EthEventLogTest.getErc1155BatchLogObjectList().get(0);
//        List<String> urlList = callGetErc1155BatchUri(erc1155BatchLogObj);
//        urlList.forEach(System.out::println);
//
//        // ERC-165 接口支持测试
//        System.out.println("ERC-1155 interface supports >>> " + checkErc165Support(erc1155BatchLogObj));
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

        System.out.println("ERC-721-Code >>>" + encode.substring(0, 10));

        Transaction reqTxn = Transaction.createEthCallTransaction(logObject.getAddress(), logObject.getAddress(), encode);

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        return FunctionReturnDecoder.decode(callResult.getValue(), tokenUriFunc.getOutputParameters());
    }

    private String callGetErc1155SingleUri(EthLog.LogObject logObject) throws IOException {
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

    /**
     * 明确请求ERC-165接口, 以确保合约符合ERC-721规范
     */
    private static boolean checkErc165Support(EthLog.LogObject logObject) throws IOException {

        // get interface id
        Bytes4 bytes4 = new Bytes4(new byte[]{13, 23, -2, 3});

        Function tmp = new Function(
                "supportsInterface",
                Collections.singletonList(bytes4),
                Collections.singletonList(TypeReference.create(Bool.class))
        );

        String tmpEncode = FunctionEncoder.encode(tmp);
        String erc165_SupportsInterfaceId = tmpEncode.substring(0, 10);

        System.out.println("Method Interface Id >>> " + erc165_SupportsInterfaceId);


        // calling the contract
        Bytes4 callingUse = new Bytes4(Numeric.hexStringToByteArray(erc165_SupportsInterfaceId));
        Function supportFunc = new Function(
                "supportsInterface",
                Collections.singletonList(callingUse),
                Collections.singletonList(TypeReference.create(Bool.class))
        );
        String encodeData = FunctionEncoder.encode(supportFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(logObject.getAddress(), logObject.getAddress(), encodeData);

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        List<Type> decode = FunctionReturnDecoder.decode(callResult.getValue(), supportFunc.getOutputParameters());

        return decode.size() > 0 && (boolean) decode.get(0).getValue();
    }

}
