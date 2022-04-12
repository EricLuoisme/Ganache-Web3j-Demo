package com.example.ganacheweb3jdemo.web3j;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;

import java.util.Arrays;
import java.util.Collections;


public class RemoteSubscribeTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";


    public static void main(String[] args) throws Exception {

        RemoteSubscribeTest testInstance = new RemoteSubscribeTest();

        Web3j web3j = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));
        testInstance.ethLogSubscription(web3j);


    }


    private void ethLogSubscription(Web3j web3j) {

        Event transferEvent = new Event("Transfer",
                Arrays.asList(
                        // from
                        TypeReference.create(Address.class),
                        // to
                        TypeReference.create(Address.class),
                        // amount / tokenId
                        TypeReference.create(Uint256.class)
                ));
        // encode event to topic
        String transferTopic = EventEncoder.encode(transferEvent);

        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST,
                Collections.emptyList());

        filter.addSingleTopic(transferTopic);

         web3j.ethLogFlowable(filter).subscribe(log -> {
             if (log.getTopics().size() > 3) {
                 System.out.println("ERC-721");
             } else {
                 System.out.println("ERC-20");
             }
         });


    }

}
