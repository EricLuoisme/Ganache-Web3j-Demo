package com.own.third.api.moralis;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author Roylic
 * 2023/7/25
 */
@Data
public class Token {
    @JSONField(name = "token_address")
    private String tokenAddress;
    private String name;
    private String symbol;
    private Object logo;
    private Object thumbnail;
    private Integer decimals;
    private String balance;
    @JSONField(name = "possible_spam")
    private Boolean possibleSpam;
}
