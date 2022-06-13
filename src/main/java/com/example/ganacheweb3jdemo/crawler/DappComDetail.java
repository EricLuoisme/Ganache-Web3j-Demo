package com.example.ganacheweb3jdemo.crawler;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author Roylic
 * 2022/6/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DappComDetail {

    /** dap info */
    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("描述")
    private String description;

    @ExcelProperty("类别")
    private String category;

    @ExcelProperty("支持链")
    private String chains;

    @ExcelProperty("是否有dapp的token")
    private Boolean has_token;

    /** dap statistics */

    // social
    @ExcelProperty("社交活跃matrix")
    private Integer social_signal;
    @ExcelProperty("社交活跃matrix增量")
    private BigDecimal social_signal_gr;

    // 24h
    @ExcelProperty("用户量-24h")
    private Integer user_24h;
    @ExcelProperty("数量-24h")
    private Integer amount_24h;
    @ExcelProperty("资产量-24h")
    private BigDecimal volume_24h;
    @ExcelProperty("usd量-24h")
    private BigDecimal usd_24h;
    @ExcelProperty("用户增量-24h")
    private BigDecimal user_24h_gr;
    @ExcelProperty("数量增量-24h")
    private BigDecimal amount_24h_gr;
    @ExcelProperty("资产增量-24h")
    private BigDecimal volume_24h_gr;
    @ExcelProperty("usd增量-24h")
    private BigDecimal usd_24h_gr;

    // 7d
    @ExcelProperty("用户量-7d")
    private Integer user_7d;
    @ExcelProperty("数量-7d")
    private Integer amount_7d;
    @ExcelProperty("资产量-7d")
    private BigDecimal volume_7d;
    @ExcelProperty("usd量-7d")
    private BigDecimal usd_7d;
    @ExcelProperty("用户增量-7d")
    private BigDecimal user_7d_gr;
    @ExcelProperty("数量增量-7d")
    private BigDecimal amount_7d_gr;
    @ExcelProperty("资产增量-7d")
    private BigDecimal volume_7d_gr;
    @ExcelProperty("usd增量-7d")
    private BigDecimal usd_7d_gr;

    // 30d
    @ExcelProperty("用户量-30d")
    private Integer user_30d;
    @ExcelProperty("数量-30d")
    private Integer amount_30d;
    @ExcelProperty("资产量-30d")
    private BigDecimal volume_30d;
    @ExcelProperty("usd量-30d")
    private BigDecimal usd_30d;
    @ExcelProperty("用户增量-30d")
    private BigDecimal user_30d_gr;
    @ExcelProperty("数量增量-30d")
    private BigDecimal amount_30d_gr;
    @ExcelProperty("资产增量-30d")
    private BigDecimal volume_30d_gr;
    @ExcelProperty("usd增量-30d")
    private BigDecimal usd_30d_gr;
}
