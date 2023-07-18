package com.own.third.api.alchemy;

import lombok.Data;

import java.util.List;

@Data
public class AddressNFTBalanceConcise {
    private List<ConciseNFTMetadata> ownedNfts;
    private int totalCount;
    private AddressNFTBalance.ValidAt validAt;
    private String pageKey;

    @Data
    public static class ConciseNFTMetadata {
        private String contractAddress;
        private String tokenId;
        private String balance;
    }
}