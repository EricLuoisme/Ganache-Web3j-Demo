package com.example.web3j.combination.solana.dto.metaplex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Roylic
 * 2023/3/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaplexStandardJsonObj {
    private String name;
    private String symbol;
    private String description;
    private String image;
    private String animation_url;
    private String external_url;
}
