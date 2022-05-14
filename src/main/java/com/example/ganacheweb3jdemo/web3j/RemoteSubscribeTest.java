package com.example.ganacheweb3jdemo.web3j;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;

import java.util.Collections;


public class RemoteSubscribeTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";

    public static final Web3j web3j = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));


    public static void main(String[] args) {

        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST,
                Collections.emptyList());

        filter.addOptionalTopics(EthEventTopics.getTopicStr(EthEventTopics.TRANSFER_TOPIC_ERC_1155_BATCH));

        System.out.println("Start listening:\n");

        web3j.ethLogFlowable(filter).subscribe(log -> {

            // 输出
            System.out.println("\n\nData for ERC-1155 Batch Transfer >>> ");
            System.out.println("removed >>" + log.isRemoved());
            System.out.println("log index >>" + log.getLogIndex());
            System.out.println("txn index >>" + log.getTransactionIndex());
            System.out.println("txn hash >>" + log.getTransactionHash());
            System.out.println("block hash >>" + log.getBlockHash());
            System.out.println("block num >>" + log.getBlockNumber());
            System.out.println("address >>" + log.getAddress());
            System.out.println("data >>" + log.getData());
            System.out.println("type >>" + log.getType());
            System.out.println("topics >>>> ");
            log.getTopics().forEach(System.out::println);
            System.out.println();
            System.out.println();
        });
    }
}
