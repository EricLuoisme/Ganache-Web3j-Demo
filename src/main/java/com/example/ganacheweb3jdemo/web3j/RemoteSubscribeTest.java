package com.example.ganacheweb3jdemo.web3j;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
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
import java.util.Optional;


public class RemoteSubscribeTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";


    public static void main(String[] args) throws Exception {

        RemoteSubscribeTest testInstance = new RemoteSubscribeTest();

        Web3j web3j = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));
        testInstance.ethLogSubscription(web3j);


    }


    private void ethLogSubscription(Web3j web3j) {

        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST,
                Collections.emptyList());

        filter.addSingleTopic(EthEventTopics.TRANSFER_TOPIC_ERC_1155_BATCH.topicStr);


         web3j.ethLogFlowable(filter).subscribe(log -> {

             if (log.getTopics().size() > 3) {
                 System.out.println("ERC-1155 BATCH");
             }

         });


    }

    /**
     * Eth Event Log Topic String 枚举
     *
     * @author Roylic
     * @date 2022/4/12
     */
    public enum EthEventTopics {

        /**
         * ERC-20 / ERC-721 transfer event topic
         */
        TRANSFER_TOPIC_ERC_20_721(EventEncoder.encode(new Event("Transfer",
                Arrays.asList(
                        // from
                        TypeReference.create(Address.class),
                        // to
                        TypeReference.create(Address.class),
                        // amount / tokenId
                        TypeReference.create(Uint256.class))
        ))),

        /**
         * ERC-1155 single transfer event topic
         */
        TRANSFER_TOPIC_ERC_1155_SINGLE(EventEncoder.encode(new Event("TransferSingle",
                Arrays.asList(
                        // operator
                        TypeReference.create(Address.class),
                        // from
                        TypeReference.create(Address.class),
                        // to
                        TypeReference.create(Address.class),
                        // tokenId
                        TypeReference.create(Uint256.class),
                        // amount
                        TypeReference.create(Uint256.class))
        ))),

        /**
         * ERC-1155 batch transfer event topic
         */
        TRANSFER_TOPIC_ERC_1155_BATCH(EventEncoder.encode(new Event("TransferBatch",
                Arrays.asList(
                        // operator
                        TypeReference.create(Address.class),
                        // from
                        TypeReference.create(Address.class),
                        // to
                        TypeReference.create(Address.class),
                        // tokenIds
                        new TypeReference<DynamicArray<Uint256>>() {
                        },
                        // amounts
                        new TypeReference<DynamicArray<Uint256>>() {
                        })
        )));

        /** Topic String for an event */
        public final String topicStr;

        EthEventTopics(String topicStr) {
            this.topicStr = topicStr;
        }

        /**
         * find topic enum by topic string
         */
        public static Optional<EthEventTopics> getEnumByStr(String inputStr) {
            return Arrays.stream(EthEventTopics.values())
                    .filter(ethEventTopics -> ethEventTopics.topicStr.equals(inputStr))
                    .findFirst();
        }
    }

}
