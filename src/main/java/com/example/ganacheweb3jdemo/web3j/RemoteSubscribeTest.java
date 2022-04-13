package com.example.ganacheweb3jdemo.web3j;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class RemoteSubscribeTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";


    public static void main(String[] args) throws Exception {
        RemoteSubscribeTest testInstance = new RemoteSubscribeTest();
        Web3j web3j = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));
        testInstance.ethLogSubscription(web3j);
    }

    private void ethLogSubscription(Web3j web3j) throws IOException {

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

    /**
     * Eth Event Log Topic String 枚举
     *
     * @author Roylic
     * @date 2022/4/12
     */
    public enum EthEventTopics {

        /**
         * ERC-20 & ERC-721 share same transfer event topic
         */
        TRANSFER_TOPIC_ERC_20_721(new Event("Transfer",
                Arrays.asList(
                        // from
                        TypeReference.create(Address.class, true),
                        // to
                        TypeReference.create(Address.class, true),
                        // non-indexed:amount / indexed:tokenId
                        TypeReference.create(Uint256.class))
        )),

        /**
         * ERC-1155 single transfer event topic
         */
        TRANSFER_TOPIC_ERC_1155_SINGLE(new Event("TransferSingle",
                Arrays.asList(
                        // operator
                        TypeReference.create(Address.class, true),
                        // from
                        TypeReference.create(Address.class, true),
                        // to
                        TypeReference.create(Address.class, true),
                        // tokenId
                        TypeReference.create(Uint256.class),
                        // amount
                        TypeReference.create(Uint256.class))
        )),

        /**
         * ERC-1155 batch transfer event topic
         */
        TRANSFER_TOPIC_ERC_1155_BATCH(new Event("TransferBatch",
                Arrays.asList(
                        // operator
                        TypeReference.create(Address.class, true),
                        // from
                        TypeReference.create(Address.class, true),
                        // to
                        TypeReference.create(Address.class, true),
                        // tokenIds
                        new TypeReference<DynamicArray<Uint256>>() {
                        },
                        // amounts
                        new TypeReference<DynamicArray<Uint256>>() {
                        })
        ));

        /**
         * Topic String for an event
         */
        public final Event event;

        EthEventTopics(Event event) {
            this.event = event;
        }

        public static String getTopicStr(EthEventTopics eventTopics) {
            return EventEncoder.encode(eventTopics.event);
        }

        /**
         * find topic enum by topic string
         */
        public static Optional<EthEventTopics> getEnumByStr(String inputStr) {
            return Arrays.stream(EthEventTopics.values())
                    .filter(ethEventTopics -> getTopicStr(ethEventTopics).equals(inputStr))
                    .findFirst();
        }
    }

}
