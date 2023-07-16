package com.own.third.api.alchemy;

import lombok.Data;

import java.util.List;

@Data
public class AddressNFTBalance {
    private List<NFTMetadata> nftMetadata;
    private int totalCount;
    private String blockHash;
}