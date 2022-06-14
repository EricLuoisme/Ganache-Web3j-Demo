package com.example.ganacheweb3jdemo.crawler;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @ExcelProperty("Slug")
    private String slug;

    @ExcelProperty("是否上新")
    private Boolean isNew;

    @ExcelIgnore
    private String godzillaId;

    @ExcelProperty("logo-link")
    private String logo;

    @ExcelProperty("dapp地址")
    private String deepLink;

    @ExcelProperty("是否移动端友好")
    private Boolean mobileFriendly;

    @ExcelProperty("featured")
    private Boolean featured;

    @ExcelIgnore
    private Map<String, String> slugs;

    @ExcelIgnore
    private List<String> protocols;

    @ExcelProperty("支持的protocol")
    private String supportProtocols;

    @ExcelIgnore
    private List<String> activeProtocols;

    @ExcelProperty("生效的protocol")
    private String activeSupportProtocols;

    @ExcelProperty("类别")
    private String category;

    @ExcelProperty("tracked")
    private Boolean tracked;

    // statistics
    @ExcelProperty("资产")
    private Integer balance;
    // total value of assets in dapp's smart contracts
    @ExcelProperty("资产（法币）")
    private Integer balanceInFiat;
    @ExcelProperty("总资产（法币）")
    private BigDecimal totalBalanceInFiat;
    @ExcelProperty("变动图表")
    private String graph;
    @ExcelProperty("汇率")
    private Integer exchangeRate;
    @ExcelProperty("currency")
    private String currencyName;
    @ExcelProperty("tx总数")
    private Integer transactionCount;
    // default sorting according to this
    @ExcelProperty("用户活跃数")
    private Integer userActivity;
    @ExcelProperty("数量（法币）")
    private Integer volumeInFiat;
    // total amount of incoming value to dapp's smart contracts
    @ExcelProperty("总数量（法币）")
    private BigDecimal totalVolumeInFiat;
    @ExcelProperty("总数量增量（法币）")
    private BigDecimal totalVolumeChangeInFiat;


    // changes
    @ExcelProperty("日活增量")
    private String dauStatus;
    @ExcelProperty("日活状态")
    private String dauLabel;

    @ExcelProperty("数量增量")
    private String volumeStatus;
    @ExcelProperty("数量状态")
    private String volumeLabel;

    @ExcelProperty("tx增量")
    private String txStatus;
    @ExcelProperty("tx状态")
    private String txLabel;

    @ExcelProperty("token数量增量")
    private String tokenVolumeStatus;
    @ExcelProperty("token数量状态")
    private String tokenVolumeLabel;

    @ExcelProperty("总数量增量")
    private String totalVolumeStatus;
    @ExcelProperty("总数量状态")
    private String totalVolumeLabel;

    @ExcelProperty("总资金增量")
    private String totalBalanceStatus;
    @ExcelProperty("总资金状态")
    private String totalBalanceLabel;
}
