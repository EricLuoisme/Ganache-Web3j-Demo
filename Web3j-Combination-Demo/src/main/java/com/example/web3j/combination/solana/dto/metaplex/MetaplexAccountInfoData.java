package com.example.web3j.combination.solana.dto.metaplex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Roylic
 * 2023/3/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaplexAccountInfoData {

    private String key;
    private String updateAuthority;
    private String mint;
    private String name;
    private String symbol;
    private String uri;
    private Integer sellerBasicPoints;
    private List<Creator> creatorList;
    private Boolean primarySaleHappened;
    private Boolean isMutable;
    private Integer editionNonce;
    private BigInteger tokenStandard;
    private Collection collection;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creator {
        private String address;
        private Boolean verified;
        private Integer share;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Collection {
        private Boolean verified;
        private String collectionMintAccount;
    }


}
