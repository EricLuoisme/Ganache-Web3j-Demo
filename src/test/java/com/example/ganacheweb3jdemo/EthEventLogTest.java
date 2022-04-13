package com.example.ganacheweb3jdemo;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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




    @Test
    public void parseErc20Log() throws IOException {

        // ERC-20
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

        // Parsing
        List<Type> amountDecode = FunctionReturnDecoder.decode(eth20.getData(), Utils.convert(UINT256_OUTPUT));
        System.out.println("Data field : " + amountDecode.get(0).getValue());

        List<Type> fromAddDecode = FunctionReturnDecoder.decode(eth20.getTopics().get(1), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("From Address : " + fromAddDecode.get(0).getValue());

        List<Type> toAddDecode = FunctionReturnDecoder.decode(eth20.getTopics().get(2), Utils.convert(ADDRESS_OUTPUT));
        System.out.println("To Address : " + toAddDecode.get(0).getValue());

        // Requesting
        EthTransaction transactionResult = web3j.ethGetTransactionByBlockHashAndIndex(eth20.getBlockHash(), eth20.getTransactionIndex()).send();

        System.out.println(transactionResult.getTransaction());

    }

    @Test
    public void parseErc721Log() {
        // ERC-721 Log
        EthLog.LogObject logObject_721 = new EthLog.LogObject();
        logObject_721.setRemoved(false);
        logObject_721.setLogIndex("0x2");
        logObject_721.setTransactionIndex("0x0");
        logObject_721.setTransactionHash("0x83729b2e8b203b7fbf492ec13d44c29a2db28b4c569fa546b2ea7379dee341f6");
        logObject_721.setBlockHash("0xe3333874ff155f62128a3a587fe97e50956da6145e04a9edc4a3bfc44f5ae42c");
        logObject_721.setBlockNumber("0x1d4ca9c");
        logObject_721.setAddress("0xf5de760f2e916647fd766b4ad9e85ff943ce3a2b");
        logObject_721.setData("0x");
        logObject_721.setType("mined");
        logObject_721.setTopics(Stream.of(
                        "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                        "0x0000000000000000000000000000000000000000000000000000000000000000",
                        "0x0000000000000000000000006dcdfa62f1c4cae2ea4e0e568d7850408be1435f",
                        "0x00000000000000000000000000000000000000000000000000000000000ca8b5")
                .collect(Collectors.toList()));


        Function addressFunc = new Function("from", new ArrayList<>(), Stream.of(TypeReference.create(Address.class)).collect(Collectors.toList()));
        List<Type> fromList = FunctionReturnDecoder.decode(logObject_721.getTopics().get(1), addressFunc.getOutputParameters());
        List<Type> toList = FunctionReturnDecoder.decode(logObject_721.getTopics().get(2), addressFunc.getOutputParameters());


        List<TypeReference<?>> outputParameters = Stream.of(TypeReference.create(Uint256.class))
                .collect(Collectors.toList());

        List<Type> TokenIdList_1 = FunctionReturnDecoder.decode(logObject_721.getTopics().get(3), Utils.convert(outputParameters));
        System.out.println(TokenIdList_1.get(0).getValue());
    }


    @Test
    public void parseErc1155SingleLog() {

        // ERC-1155 Single Log
        EthLog.LogObject logObject_1155_single = new EthLog.LogObject();
        logObject_1155_single.setRemoved(false);
        logObject_1155_single.setLogIndex("0xa");
        logObject_1155_single.setTransactionIndex("0x5");
        logObject_1155_single.setTransactionHash("0x88668d859d136057c576915e63b1038707a5af0d8230350c9462a8632f19783e");
        logObject_1155_single.setBlockHash("0x1483a152bd8f91a846df198def6f4655e8abd06ebfd8808fb7f0d36fe505db4b");
        logObject_1155_single.setBlockNumber("0x1d4caa3");
        logObject_1155_single.setAddress("0xcc57b6d9768e05e8cfb6081ec0f1cb4635e1548d");
        logObject_1155_single.setData("0x467fcabdddf8a5e4ddcfb6bc056755e5adfa099b560aca0cfe8afe071e2717050000000000000000000000000000000000000000000000056bc75e2d63100000");
        logObject_1155_single.setType("mined");
        logObject_1155_single.setTopics(Stream.of(
                        "0xc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62",
                        "0x000000000000000000000000723a9bb7abacf54e46b2b31ecfcca3b5921b9d52",
                        "0x0000000000000000000000000000000000000000000000000000000000000000",
                        "0x000000000000000000000000723a9bb7abacf54e46b2b31ecfcca3b5921b9d52")
                .collect(Collectors.toList()));
    }

    @Test
    public void parseErc1155BatchLog() {

    }

}
