package com.own.third.api.alchemy.dto;

import lombok.Data;

import java.util.List;

@Data
public class NFTMetadata {
    private Contract contract;
    private Id id;
    private String balance;
    private String title;
    private String description;
    private TokenUri tokenUri;
    private List<Media> media;
    private Metadata metadata;
    private String timeLastUpdated;
    private ContractMetadata contractMetadata;

    // extra
    private String usedTokenId;


    @Data
    public static class Contract {
        private String address;
    }

    @Data
    public static class Id {
        private String tokenId;
        private NftTokenMetadata tokenMetadata;
    }

    @Data
    public static class NftTokenMetadata {
        private String tokenType;
    }

    @Data
    public static class TokenUri {
        private String gateway;
        private String raw;
    }

    @Data
    public static class Media {
        private String gateway;
        private String thumbnail;
        private String raw;
        private String format;
        private int bytes;
    }

    @Data
    public static class Metadata {
        private String name;
        private String description;
        private String image;
        private List<Attribute> attributes;
    }

    @Data
    public static class Attribute {
        private String value;
        private String trait_type;
    }

    @Data
    public static class ContractMetadata {
        private String name;
        private String symbol;
        private String totalSupply;
        private String tokenType;
        private String contractDeployer;
        private long deployedBlockNumber;
        private OpenSea openSea;
    }

    @Data
    public static class OpenSea {
        private String lastIngestedAt;
    }
}

