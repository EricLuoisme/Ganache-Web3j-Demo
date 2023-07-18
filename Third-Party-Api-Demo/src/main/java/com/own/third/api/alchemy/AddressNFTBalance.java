package com.own.third.api.alchemy;

import lombok.Data;

import java.util.List;

@Data
public class AddressNFTBalance {
    private List<NFTMetadata> ownedNfts;
    private int totalCount;
    private ValidAt validAt;
    private String pageKey;

    @Data
    public static class ValidAt {
        private int blockNumber;
        private String blockHash;
        private String blockTimestamp;
    }
}