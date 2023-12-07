package com.own.third.api.binance;

import com.alibaba.fastjson2.JSONObject;
import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BinanceOrderTest {


    private final ObjectMapper om = new ObjectMapper();


    private final String TEST_URL = "https://testnet.binance.vision";
    private final String TEST_API = "";
    private final String TEST_SRC = "";


    @Test
    public void testConnection() {
        SpotClientImpl spotClient = new SpotClientImpl(TEST_API, TEST_SRC, TEST_URL);
        String ping = spotClient.createMarket().ping();
        System.out.println(ping);
    }

    @Test
    public void testAccInfo() throws JsonProcessingException {
        SpotClientImpl spotClient = new SpotClientImpl(TEST_API, TEST_SRC, TEST_URL);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("timestamp", Instant.now().toEpochMilli());
        String accountInfoResp = spotClient.createTrade().account(reqMap);
        JSONObject jsonObject = JSONObject.parseObject(accountInfoResp);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }

}
