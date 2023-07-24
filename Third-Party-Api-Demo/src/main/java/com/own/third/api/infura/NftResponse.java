package com.own.third.api.infura;

import lombok.Data;

import java.util.List;

/**
 * @author Roylic
 * 2023/7/24
 */
@Data
public class NftResponse {
    private int total;
    private int pageNumber;
    private int pageSize;
    private String network;
    private String account;
    private String cursor;
    private List<NFTAsset> assets;

    @Data
    public static class NFTAsset {
        private String contract;
        private String tokenId;
        private String supply;
        private String type;
        private NFTMetadata metadata;
    }

    @Data
    public static class NFTMetadata {
        private String name;
        private String description;
        private String image;
    }
}
