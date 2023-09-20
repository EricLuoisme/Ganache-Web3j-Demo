package com.own.third.api.moralis;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author Roylic
 * 2023/9/5
 */
@Data
public class NftEntity {

    private int page;
    private int pageSize;
    private String cursor;
    private List<NftResult> result;
    private String status;

    @Data
    public static class NftResult {
        @JSONField(name = "token_address")
        private String tokenAddress;
        @JSONField(name = "token_id")
        private String tokenId;
        private String amount;
        @JSONField(name = "owner_of")
        private String ownerOf;
        @JSONField(name = "possible_spam")
        private String tokenHash;
        @JSONField(name = "block_number_minted")
        private String blockNumberMinted;
        @JSONField(name = "block_number")
        private String blockNumber;
        @JSONField(name = "possible_spam")
        private boolean possibleSpam;
        @JSONField(name = "contract_type")
        private String contractType;
        private String name;
        private String symbol;
        @JSONField(name = "token_uri")
        private String tokenUri;
        private String metadata;
        @JSONField(name = "last_token_uri_sync")
        private String lastTokenUriSync;
        @JSONField(name = "last_metadata_sync")
        private String lastMetadataSync;
        @JSONField(name = "minter_address")
        private String minterAddress;
        @JSONField(name = "verified_collection")
        private boolean verifiedCollection;

        private Media media;
    }

    @Data
    public static class Media {
        private String mimetype;
        @JSONField(name = "parent_hash")
        private String parentHash;
        private String status;
        @JSONField(name = "updated_at")
        private String updatedAt;
        @JSONField(name = "media_collection")
        private MediaCollection mediaCollection;
        @JSONField(name = "original_media_url")
        private String originalMediaUrl;
    }

    @Data
    public static class MediaCollection {
        private MediaInfo low;
        private MediaInfo medium;
        private MediaInfo high;
    }

    @Data
    public static class MediaInfo {
        private int height;
        private int width;
        private String url;
    }
}

