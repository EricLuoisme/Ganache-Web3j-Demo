package com.example.ganacheweb3jdemo.crawler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author Roylic
 * 2022/6/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DappComDetail {

    /** dap info */

    private String name;

    private String description;

    private String category;

    private Set<String> chains;

    private Boolean has_token;

    /** dap statistics */

    // 24h
    private Integer user_24h;
    private Integer amount_24h;
    private BigDecimal volume_24h;
    private BigDecimal usd_24h;
    private BigDecimal user_24h_gr;
    private BigDecimal amount_24h_gr;
    private BigDecimal volume_24h_gr;
    private BigDecimal usd_24h_gr;
    // 7d
    private Integer user_7d;
    private Integer amount_7d;
    private BigDecimal volume_7d;
    private BigDecimal usd_7d;
    private BigDecimal user_7d_gr;
    private BigDecimal amount_7d_gr;
    private BigDecimal volume_7d_gr;
    private BigDecimal usd_7d_gr;

    // 30d
    private Integer user_30d;
    private Integer amount_30d;
    private BigDecimal volume_30d;
    private BigDecimal usd_30d;
    private BigDecimal user_30d_gr;
    private BigDecimal amount_30d_gr;
    private BigDecimal volume_30d_gr;
    private BigDecimal usd_30d_gr;

    // social
    private Integer social_signal;
    private BigDecimal social_signal_gr;
}
