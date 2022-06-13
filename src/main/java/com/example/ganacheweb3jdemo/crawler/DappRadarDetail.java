package com.example.ganacheweb3jdemo.crawler;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 接收DApp信息实体
 *
 * @author Roylic
 * 2022/6/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DappRadarDetail {

    @ExcelIgnore
    private Integer id;

    @ExcelProperty("名称")
    private String name;
    @ExcelProperty
    private String slug;
    @ExcelProperty("是否上新")
    private Boolean isNew;



    private Statistic statistic;
    private String godzillaId;
    private String logo;
    private String deepLink;
    private Boolean mobileFriendly;
    private Boolean featured;
    private Map<String, String> slugs;
    private List<String> protocols;
    private List<String> activeProtocols;
    private String category;
    private Boolean tracked;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistic {
        private Integer balance;
        // total value of assets in dapp's smart contracts
        private Integer balanceInFiat;
        private Integer totalBalanceInFiat;
        private String graph;
        private Integer exchangeRate;
        private String currencyName;
        private Integer transactionCount;
        // default sorting according to this
        private Integer userActivity;
        private Integer volumeInFiat;
        // total amount of incoming value to dapp's smart contracts
        private Integer totalVolumeInFiat;
        private Integer totalVolumeChangeInFiat;
        private Changes changes;
    }

    /**
     * contains: label -> {positive, negative}, status -> {+33.2%, -110.55%}
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Changes {
        // daily activate user
        private Map<String, String> dau;
        private Map<String, String> volume;
        private Map<String, String> tx;
        private Map<String, String> tokenVolume;
        private Map<String, String> totalVolume;
        private Map<String, String> totalBalance;
    }

}
