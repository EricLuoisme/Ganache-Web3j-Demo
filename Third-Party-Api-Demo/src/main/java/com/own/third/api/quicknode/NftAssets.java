package com.own.third.api.quicknode;

import lombok.Data;

import java.util.List;

@Data
public class NftAssets {
    private String owner;
    private List<NftMetadata> assets;
    private int totalPages;
    private int totalItems;
    private int pageNumber;

    @Data
    public static class NftMetadata {
        private String collectionName;
        private String collectionTokenId;
        private String collectionAddress;
        private String name;
        private String description;
        private String imageUrl; // here the img is from QuickNode, not the real IMG
        private String chain;
        private String network;
    }
}
