package com.example.ganacheweb3jdemo;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthLog;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EthEventLogTest {

    @Test
    public void formLog() {
        EthLog.LogObject eth20 = new EthLog.LogObject();
        eth20.setRemoved(false);
        eth20.setLogIndex("0x0");
        eth20.setTransactionIndex("0x0");
        eth20.setTransactionHash("0x4d4c9e968e955ff7a688fa5e275211cdc09f995566b2f762a8adf23e02e3a58a");
        eth20.setBlockHash("0xdcf299f86756f3cbc8664687dc82f68eb831fab42881595f59ea1a122bf29c7b");
        eth20.setBlockNumber("0x1d8abff");
        eth20.setAddress("0xa36085f69e2889c224210f603d836748e7dc0088");
        eth20.setData("0x0000000000000000000000000000000000000000000000008ac7230489e80000");
        eth20.setType("mined");
        eth20.setTopics(
                Stream.of(
                        "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                        "0x0000000000000000000000004281ecf07378ee595c564a59048801330f3084ee",
                        "0x000000000000000000000000943ad1ea9b8efac0c039a2325cf1ec7b0cc57ec1")
        .collect(Collectors.toList()));


        List<TypeReference<?>> outputParameters = Stream.of(TypeReference.create(Uint256.class))
                .collect(Collectors.toList());

        List<Type> decode = FunctionReturnDecoder.decode(eth20.getData(), Utils.convert(outputParameters));
        Type type = decode.get(0);
        System.out.println(type);


    }

}
