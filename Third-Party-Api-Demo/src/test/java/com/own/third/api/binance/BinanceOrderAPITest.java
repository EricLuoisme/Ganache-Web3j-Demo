package com.own.third.api.binance;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BinanceOrderAPITest {


    private final ObjectMapper om = new ObjectMapper();


    private static final String TEST_URL = "https://testnet.binance.vision";
    private static final String TEST_API = "";
    private static final String TEST_SRC = "";

    private static final SpotClientImpl TESTNET_SPOT_CLIENT = new SpotClientImpl(TEST_API, TEST_SRC, TEST_URL);

    private static final SpotClientImpl MAINNET_SPOT_CLIENT = new SpotClientImpl();


    @Test
    public void testAccInfo() throws JsonProcessingException {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("timestamp", Instant.now().toEpochMilli());
        String accountInfoResp = TESTNET_SPOT_CLIENT.createTrade().account(reqMap);
        JSONObject jsonObject = JSONObject.parseObject(accountInfoResp);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }


    @Test
    public void allOrders() throws JsonProcessingException {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("symbol", "BTCUSDT");
        reqMap.put("timestamp", Instant.now().toEpochMilli());
        String resp = TESTNET_SPOT_CLIENT.createTrade().getOrders(reqMap);
        JSONArray jsonArray = JSONArray.parseArray(resp);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonArray));
    }

    @Test
    public void placeOrder() throws JsonProcessingException {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("symbol", "BTCUSDT");
        reqMap.put("side", "BUY");
        reqMap.put("type", "MARKET");

        // use quantity under MARKET price order, we may need to pay more/less USDT
        // the quantity must within the range from Lot_Size, which can be retrieved from PairInfo
        // and the stepSize also need to be considered
        reqMap.put("quantity", "0.0002");

        reqMap.put("timestamp", Instant.now().toEpochMilli());
        String resp = TESTNET_SPOT_CLIENT.createTrade().newOrder(reqMap);
        JSONObject jsonObject = JSONObject.parseObject(resp);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }

    @Test
    public void querySpecificOrder() throws JsonProcessingException {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("symbol", "BTCUSDT");
        reqMap.put("orderId", "746260");
        reqMap.put("timestamp", Instant.now().toEpochMilli());
        String resp = TESTNET_SPOT_CLIENT.createTrade().getOrder(reqMap);
        JSONObject jsonObject = JSONObject.parseObject(resp);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }


}
