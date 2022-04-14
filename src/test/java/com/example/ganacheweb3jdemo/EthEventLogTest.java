package com.example.ganacheweb3jdemo;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EthEventLogTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK
            = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";

    public static final Web3j web3j
            = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));

    public static final List<TypeReference<?>> UINT256_OUTPUT
            = Stream.of(TypeReference.create(Uint256.class)).collect(Collectors.toList());

    public static final List<TypeReference<?>> ADDRESS_OUTPUT
            = Stream.of(TypeReference.create(Address.class)).collect(Collectors.toList());

    public static final List<TypeReference<?>> UINT256_LIST_OUTPUT
            = Stream.of(new TypeReference<DynamicArray<Uint256>>() {
    }).collect(Collectors.toList());


    @Test
    public void parseErc20Log() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // ERC-20
        EthLog.LogObject eth20 = getErc20LogObj();

        // Parsing
        List<Type> amountDecode = FunctionReturnDecoder.decode(eth20.getData(), Utils.convert(UINT256_OUTPUT));
        System.out.println("Data field : " + amountDecode.get(0).getValue());

        List<Type> fromAddDecode = FunctionReturnDecoder.decode(eth20.getTopics().get(1), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("From Address : " + fromAddDecode.get(0).getValue());

        List<Type> toAddDecode = FunctionReturnDecoder.decode(eth20.getTopics().get(2), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("To Address : " + toAddDecode.get(0).getValue());

        // Requesting
        EthTransaction transactionResult = web3j.ethGetTransactionByBlockHashAndIndex(eth20.getBlockHash(), eth20.getTransactionIndex()).send();

        // Decode transaction input
        Transaction transaction = transactionResult.getTransaction().get();
        String inputData = transaction.getInput();

        String method = inputData.substring(0, 10);
        System.out.println("Method >>>>>> " + method);
        String to = inputData.substring(10, 74);
        String value = inputData.substring(74);
        Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
        refMethod.setAccessible(true);
        Address address = (Address) refMethod.invoke(null, to, 0, Address.class);
        System.out.println("Address >>>>>> " + address.toString());
        Uint256 amount = (Uint256) refMethod.invoke(null, value, 0, Uint256.class);
        System.out.println("amount >>>>>> " + amount.getValue());

        System.out.println(transactionResult.getTransaction());
    }

    @Test
    public void parseErc721Log() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // ERC-721 Log
        EthLog.LogObject logObject_721 = getErc721LogObj();

        // Parsing
        List<Type> idDecode = FunctionReturnDecoder.decode(logObject_721.getTopics().get(3), Utils.convert(UINT256_OUTPUT));
        System.out.println("Token id : " + idDecode.get(0).getValue());

        List<Type> fromAddDecode = FunctionReturnDecoder.decode(logObject_721.getTopics().get(1), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("From Address : " + fromAddDecode.get(0).getValue());

        List<Type> toAddDecode = FunctionReturnDecoder.decode(logObject_721.getTopics().get(2), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("To Address : " + toAddDecode.get(0).getValue());

        // Requesting
        EthTransaction transactionResult = web3j.ethGetTransactionByBlockHashAndIndex(logObject_721.getBlockHash(), logObject_721.getTransactionIndex()).send();

        System.out.println(transactionResult.getTransaction());
    }

    @Test
    public void parseErc1155SingleLog() throws IOException {
        EthLog.LogObject logObject_1155_single = getErc1155SingleLogObj();

        // Parsing
        List<Type> dataDecode = FunctionReturnDecoder.decode(logObject_1155_single.getData(), Utils.convert(UINT256_OUTPUT));
        System.out.println("Data field : " + dataDecode.get(0).getValue());

        List<Type> fromAddDecode = FunctionReturnDecoder.decode(logObject_1155_single.getTopics().get(2), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("From Address : " + fromAddDecode.get(0).getValue());

        List<Type> toAddDecode = FunctionReturnDecoder.decode(logObject_1155_single.getTopics().get(3), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("To Address : " + toAddDecode.get(0).getValue());

        // Requesting
        EthTransaction transactionResult = web3j.ethGetTransactionByBlockHashAndIndex(logObject_1155_single.getBlockHash(), logObject_1155_single.getTransactionIndex()).send();


        List<TypeReference<Type>> nonIndexedParameters = EthEventTopics.TRANSFER_TOPIC_ERC_1155_SINGLE.event.getNonIndexedParameters();
        List<Type> decode = FunctionReturnDecoder.decode(logObject_1155_single.getData(), nonIndexedParameters);

        Uint256 tokenId = (Uint256) decode.get(0);
        System.out.println("Token Id >>> " + tokenId.getValue());

        Uint256 tokenAmount = (Uint256) decode.get(1);
        System.out.println("Token Amount >>> " + tokenAmount.getValue());
    }

    @Test
    public void parseErc1155BatchLog() throws IOException {

        EthLog.LogObject logObject_1155_batch = getErc1155BatchLogObj();

        Web3j web3j_functionx = Web3j.build(new HttpService("http://testnet-bsc-dataseed2.functionx.io:8545"));
        EthTransaction tra = web3j_functionx.ethGetTransactionByHash(logObject_1155_batch.getTransactionHash()).send();

        List<TypeReference<Type>> nonIndexedParameters = EthEventTopics.TRANSFER_TOPIC_ERC_1155_BATCH.event.getNonIndexedParameters();
        List<Type> decodeArr = FunctionReturnDecoder.decode(logObject_1155_batch.getData(), nonIndexedParameters);

        // List <transferred token id>
        DynamicArray<Uint256> tokenIdDyArr = (DynamicArray<Uint256>) decodeArr.get(0);
        Iterator<Uint256> idIterator = tokenIdDyArr.getValue().iterator();

        // List <transferred token amount>
        DynamicArray<Uint256> tokenAmountList = ((DynamicArray<Uint256>) decodeArr.get(1));
        Iterator<Uint256> amountIterator = tokenAmountList.getValue().iterator();

        while (idIterator.hasNext()) {
            System.out.println();
            System.out.println(idIterator.next().getValue());
            System.out.println(amountIterator.next().getValue());
        }


    }

    @NotNull
    public static EthLog.LogObject getErc20LogObj() {
        EthLog.LogObject eth20 = new EthLog.LogObject();
        eth20.setRemoved(false);
        eth20.setLogIndex("0x0");
        eth20.setTransactionIndex("0x0");
        eth20.setTransactionHash("0x4d4c9e968e955ff7a688fa5e275211cdc09f995566b2f762a8adf23e02e3a58a");
        eth20.setBlockHash("0xdcf299f86756f3cbc8664687dc82f68eb831fab42881595f59ea1a122bf29c7b");
        eth20.setBlockNumber("0x1d8abff");
        // the address for smart contract that accept ERC-20 token
        eth20.setAddress("0xa36085f69e2889c224210f603d836748e7dc0088");
        // the amount of the ERC-20 been sent
        eth20.setData("0x0000000000000000000000000000000000000000000000008ac7230489e80000");
        eth20.setType("mined");
        eth20.setTopics(Stream.of(
                        // event keccak 256 signature
                        "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                        // address send ERC-20 token
                        "0x0000000000000000000000004281ecf07378ee595c564a59048801330f3084ee",
                        // address receive ERC-20 token
                        "0x000000000000000000000000943ad1ea9b8efac0c039a2325cf1ec7b0cc57ec1")
                .collect(Collectors.toList()));
        return eth20;
    }

    @NotNull
    public static EthLog.LogObject getErc721LogObj() {
        EthLog.LogObject logObject_721 = new EthLog.LogObject();
        logObject_721.setRemoved(false);
        logObject_721.setLogIndex("0x2");
        logObject_721.setTransactionIndex("0x0");
        logObject_721.setTransactionHash("0x83729b2e8b203b7fbf492ec13d44c29a2db28b4c569fa546b2ea7379dee341f6");
        logObject_721.setBlockHash("0xe3333874ff155f62128a3a587fe97e50956da6145e04a9edc4a3bfc44f5ae42c");
        logObject_721.setBlockNumber("0x1d4ca9c");
        // the address for smart contract that accept ERC-721 token
        logObject_721.setAddress("0xf5de760f2e916647fd766b4ad9e85ff943ce3a2b");
        logObject_721.setData("0x");
        logObject_721.setType("mined");
        logObject_721.setTopics(Stream.of(
                        // event keccak 256 signature
                        "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                        // address send ERC-721 token
                        "0x0000000000000000000000000000000000000000000000000000000000000000",
                        // address receive ERC-721 token
                        "0x0000000000000000000000006dcdfa62f1c4cae2ea4e0e568d7850408be1435f",
                        // id of ERC-721 token
                        "0x00000000000000000000000000000000000000000000000000000000000ca8b5")
                .collect(Collectors.toList()));
        return logObject_721;
    }

    @NotNull
    public static EthLog.LogObject getErc1155SingleLogObj() {
        // ERC-1155 Single Log
        EthLog.LogObject logObject_1155_single = new EthLog.LogObject();
        logObject_1155_single.setRemoved(false);
        logObject_1155_single.setLogIndex("0xa");
        logObject_1155_single.setTransactionIndex("0x5");
        logObject_1155_single.setTransactionHash("0x88668d859d136057c576915e63b1038707a5af0d8230350c9462a8632f19783e");
        logObject_1155_single.setBlockHash("0x1483a152bd8f91a846df198def6f4655e8abd06ebfd8808fb7f0d36fe505db4b");
        logObject_1155_single.setBlockNumber("0x1d4caa3");
        // the address for smart contract that accept ERC-1155 token
        logObject_1155_single.setAddress("0xcc57b6d9768e05e8cfb6081ec0f1cb4635e1548d");
        // token id
        logObject_1155_single.setData("0x467fcabdddf8a5e4ddcfb6bc056755e5adfa099b560aca0cfe8afe071e2717050000000000000000000000000000000000000000000000056bc75e2d63100000");
        logObject_1155_single.setType("mined");
        logObject_1155_single.setTopics(Stream.of(
                        // event keccak 256 signature
                        "0xc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62",
                        // address for the ERC-1155 operator
                        "0x000000000000000000000000723a9bb7abacf54e46b2b31ecfcca3b5921b9d52",
                        // address send ERC-1155 token
                        "0x0000000000000000000000000000000000000000000000000000000000000000",
                        // address receive ERC-1155 token
                        "0x000000000000000000000000723a9bb7abacf54e46b2b31ecfcca3b5921b9d52")
                .collect(Collectors.toList()));
        return logObject_1155_single;
    }

    @NotNull
    public static EthLog.LogObject getErc1155BatchLogObj() {
        // ERC-1155 Batch Log (From http://testnet-bsc-dataseed2.functionx.io:8545 Node)
        EthLog.LogObject logObject_1155_batch = new EthLog.LogObject();
        logObject_1155_batch.setRemoved(false);
        logObject_1155_batch.setLogIndex("0xb");
        logObject_1155_batch.setTransactionIndex("0x1");
        logObject_1155_batch.setTransactionHash("0xf6a17dce026ba9373b1cd89a0b7231f1e39675d7010cd84a14685360f4bb9f7c");
        logObject_1155_batch.setBlockHash("0x8b1bbb972a6b85b06fadeff37a22ce6da844ad5f0dfa94f6541be9f74f9deaea");
        logObject_1155_batch.setBlockNumber("0x3e588a");
        // the address for smart contract that accept ERC-1155 token
        logObject_1155_batch.setAddress("0x907316f56b0be6d4cb5a455eef368d3a75d08501");
        // data[0]: tokenIds, data[1]: tokenAmounts
        logObject_1155_batch.setData("0x00000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000" +
                "1a0000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000" +
                "1000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000030" +
                "0000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000005000" +
                "0000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000700000" +
                "0000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000090000000" +
                "00000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000a000000000" +
                "0000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000100000000000" +
                "0000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000010000000000000" +
                "0000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000001000000000000000" +
                "0000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000100000000000000000" +
                "000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000001");
        logObject_1155_batch.setType("null");
        logObject_1155_batch.setTopics(Stream.of(
                        // event keccak 256 signature
                        "0x4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb",
                        // address for the ERC-1155 operator
                        "0x000000000000000000000000e7698e900666687d5d0ef46ba7beaa39ca11d12c",
                        // address send ERC-1155 token
                        "0x000000000000000000000000e7698e900666687d5d0ef46ba7beaa39ca11d12c",
                        // address receive ERC-1155 token
                        "0x0000000000000000000000008cb9f475966cc409d3bb0b8f222841c65b7b8664")
                .collect(Collectors.toList()));
        return logObject_1155_batch;
    }

}
