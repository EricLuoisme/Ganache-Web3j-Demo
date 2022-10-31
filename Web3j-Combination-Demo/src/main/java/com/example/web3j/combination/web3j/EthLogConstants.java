package com.example.web3j.combination.web3j;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Numeric;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ethereum Event Log 相关枚举
 *
 * @author Roylic
 * @date 2022/4/12
 */
public class EthLogConstants {

    /** Method id for ERC-165 supportsInterface(bytes4) */
    public static final String ERC_165_SUPPORT_METHOD_ID_HEX = "0x01ffc9a7";

    /** Bytes4 input for ERC-165 supportsInterface(bytes4) */
    public static final Bytes4 ERC_165_SUPPORT_BYTES_4 = new Bytes4(Numeric.hexStringToByteArray(ERC_165_SUPPORT_METHOD_ID_HEX));

    /** ERC-20 Log Topic Minimum Number */
    public static final int ERC_20_TOPIC_NUMS = 3;

    /** ERC-721 Log Topic Minimum Number */
    public static final int ERC_721_TOPIC_NUMS = 4;

    /** ERC-1155 Log Topic Minimum Number */
    public static final int ERC_1155_TOPIC_NUMS = 4;

    public static final String ERC_20 = "20";

    public static final String ERC_721 = "721";

    public static final String ERC_1155 = "1155";

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
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        // depends
                        TypeReference.create(Uint256.class))
        )),

        /**
         * ERC-1155 single transfer event topic
         */
        TRANSFER_TOPIC_ERC_1155_SINGLE(new Event("TransferSingle",
                Arrays.asList(
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Uint256.class),
                        TypeReference.create(Uint256.class))
        )),

        /**
         * ERC-1155 batch transfer event topic
         */
        TRANSFER_TOPIC_ERC_1155_BATCH(new Event("TransferBatch",
                Arrays.asList(
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        new TypeReference<DynamicArray<Uint256>>() {},
                        new TypeReference<DynamicArray<Uint256>>() {})
        ));

        public final Event event;

        EthEventTopics(Event event) {
            this.event = event;
        }

        /**
         * get encoded topic string
         */
        public static String getTopicStr(EthEventTopics eventTopics) {
            return EventEncoder.encode(eventTopics.event);
        }

        /**
         * find topic enum by topic string
         */
        public static Optional<EthEventTopics> getEnumByStr(String inputStr) {
            return Arrays.stream(EthEventTopics.values())
                    .filter(ethEventTopics -> EthEventTopics.getTopicStr(ethEventTopics)
                            .equals(inputStr))
                    .findFirst();
        }
    }


    /**
     * 方便获取Web3参数output转换的枚举
     */
    public enum EthFuncOutput {

        /**
         * Address
         */
        ADDRESS {
            @Override
            public List<TypeReference<?>> getFuncOutputParams() {
                return Stream.of(TypeReference.create(Address.class))
                        .collect(Collectors.toList());
            }
        },

        /**
         * Token Id / Amount
         */
        UINT256 {
            @Override
            public List<TypeReference<?>> getFuncOutputParams() {
                return Stream.of(TypeReference.create(Uint256.class))
                        .collect(Collectors.toList());
            }
        };

        /**
         * get func output params for ease
         */
        public abstract List<TypeReference<?>> getFuncOutputParams();
    }
}
