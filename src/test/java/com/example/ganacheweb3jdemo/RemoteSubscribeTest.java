package com.example.ganacheweb3jdemo;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Collections;


public class RemoteSubscribeTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";

    public static final Web3j web3j = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));


    @Test
    private void ethLogSubscription() throws IOException {


        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST,
                Collections.emptyList());

        filter.addOptionalTopics(EthEventTopics.getTopicStr(EthEventTopics.TRANSFER_TOPIC_ERC_1155_BATCH));

        web3j.ethLogFlowable(filter).subscribe(log -> {
            System.out.println("Data for batch transfer >>> " + log.getData());
        });


//        EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt("0x6e92e82f89c85cc780e5032ba255edf3214a78ccaae9c61fc9d7882d7e4f13f6").send();
//
//        System.out.println(send.toString());
    }


}
