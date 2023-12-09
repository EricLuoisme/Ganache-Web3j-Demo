package com.own.third.api.binance;

import com.alibaba.fastjson2.JSONObject;
import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class BinancePriceAPITest {

    private final ObjectMapper om = new ObjectMapper();

    private static final String TEST_URL = "https://testnet.binance.vision";
    private static final String TEST_API = "";
    private static final String TEST_SRC = "";

    private static final SpotClientImpl TESTNET_SPOT_CLIENT = new SpotClientImpl(TEST_API, TEST_SRC, TEST_URL);

    private static final SpotClientImpl MAINNET_SPOT_CLIENT = new SpotClientImpl();

    @Test
    public void depthQuery() throws JsonProcessingException {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("symbol", "BTCUSDT");
        reqMap.put("limit", 10);
        String resp = TESTNET_SPOT_CLIENT.createMarket().depth(reqMap);
        JSONObject jsonObject = JSONObject.parseObject(resp);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }


}
