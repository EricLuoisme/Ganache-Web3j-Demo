package com.own.third.api.binance;

import com.alibaba.fastjson2.JSONObject;
import com.binance.connector.client.impl.SpotClientImpl;
import com.own.third.api.binance.dto.MarketDepth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BinanceUtil {


    public static Optional<MarketDepth> queryDepth(SpotClientImpl spotClient, String tradingPair, int limit) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("symbol", tradingPair);
        reqMap.put("limit", limit);
        try {
            String resp = spotClient.createMarket().depth(reqMap);
            MarketDepth marketDepth = JSONObject.parseObject(resp, MarketDepth.class);
            return Optional.of(marketDepth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


}
