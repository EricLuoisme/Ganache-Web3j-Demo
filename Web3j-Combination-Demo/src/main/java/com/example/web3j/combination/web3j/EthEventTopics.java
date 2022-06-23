package com.example.web3j.combination.web3j;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.Arrays;
import java.util.Optional;

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
