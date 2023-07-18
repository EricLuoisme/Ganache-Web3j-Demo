package com.own.third.api.alchemy;

import lombok.Data;

import java.util.List;

@Data
public class NFTMetadata {

    private Contract contract;
    private String tokenId;
    private String tokenType;
    private String name;
    private String description;
    private Image image;
    private Raw raw;
    private String tokenUri;
    private String timeLastUpdated;
    private String balance;
    private AcquiredAt acquiredAt;


    @Data
    public static class Contract {
        private String address;
        private String name;
        private String symbol;
        private String totalSupply;
        private String tokenType;
        private String contractDeployer;
        private int deployedBlockNumber;
        private OpenSeaMetadata openSeaMetadata;
        private Boolean isSpam;
        private List<String> spamClassifications;
    }

    @Data
    public static class OpenSeaMetadata {
        private String floorPrice;
        private String collectionName;
        private String safelistRequestStatus;
        private String imageUrl;
        private String description;
        private String externalUrl;
        private String twitterUsername;
        private String discordUrl;
        private String lastIngestedAt;
    }

    @Data
    public static class Image {
        private String cachedUrl;
        private String thumbnailUrl;
        private String pngUrl;
        private String contentType;
        private int size;
        private String originalUrl;
    }

    @Data
    public static class Raw {
        private String tokenUri;
        private Metadata metadata;
        private String error;
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
        private String display_type;
    }

    @Data
    public static class AcquiredAt {
        private String blockTimestamp;
        private String blockNumber;
    }
}

