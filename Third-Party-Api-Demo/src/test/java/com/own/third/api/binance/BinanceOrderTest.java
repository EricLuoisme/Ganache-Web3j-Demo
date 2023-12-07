package com.own.third.api.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import org.junit.jupiter.api.Test;

public class BinanceOrderTest {


    @Test
    public void testConnection() {
        SpotClientImpl spotClient = new SpotClientImpl();
        String time = spotClient.createMarket().time();
        System.out.println(time);
    }

}
