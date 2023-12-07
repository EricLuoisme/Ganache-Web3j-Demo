package com.own.third.api.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class BinanceOrderTest {


    private final ObjectMapper om = new ObjectMapper();


    private final String TEST_URL = "https://testnet.binance.vision";
    private final String TEST_API = "";
    private final String TEST_SRC = "";


    @Test
    public void testConnection() {
        SpotClientImpl spotClient = new SpotClientImpl(TEST_API, TEST_SRC, TEST_URL);
        String ping = spotClient.createMarket().time();
        System.out.println(ping);
    }

    @Test
    public void exchangeInfo() throws JsonProcessingException {
        SpotClientImpl spotClient = new SpotClientImpl(TEST_API, TEST_SRC, TEST_URL);



    }

}
