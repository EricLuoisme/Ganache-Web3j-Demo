package com.own.third.api.binance;

import com.alibaba.fastjson2.JSONObject;
import com.binance.connector.client.impl.SpotClientImpl;
import com.own.third.api.binance.dto.AccountInfo;
import com.own.third.api.binance.dto.MarketDepth;
import com.own.third.api.binance.dto.OrderDetail;
import com.own.third.api.binance.dto.QueryOrderDetail;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BinanceUtil {

    /**
     * Query account balance
     *
     * @param spotClient constructed client
     * @return opt
     */
    public static Optional<AccountInfo> queryBalance(SpotClientImpl spotClient) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("timestamp", Instant.now().toEpochMilli());
        try {
            String resp = spotClient.createTrade().account(reqMap);
            AccountInfo accountInfo = JSONObject.parseObject(resp, AccountInfo.class);
            return Optional.of(accountInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Query trading pair depth for price checking
     *
     * @param spotClient  constructed client
     * @param tradingPair trading pair, e.g. BTCUSDT
     * @param limit       depth
     * @return opt
     */
    public static Optional<MarketDepth> queryTradingPairDepth(SpotClientImpl spotClient, String tradingPair, int limit) {
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

    /**
     * Query specific spot order
     *
     * @param spotClient    constructed client
     * @param tradingPair   trading pair, e.g. BTCUSDT
     * @param orderNo       Binance response order no.
     * @param clientOrderNo local order no.
     * @return opt
     */
    public static Optional<QueryOrderDetail> querySpotOrder(SpotClientImpl spotClient, String tradingPair, String orderNo, String clientOrderNo) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("symbol", tradingPair);
        if (null != clientOrderNo) {
            reqMap.put("origClientOrderId", orderNo);
        } else {
            reqMap.put("orderId", orderNo);
        }
        reqMap.put("timestamp", Instant.now().toEpochMilli());
        try {
            String resp = spotClient.createTrade().getOrder(reqMap);
            QueryOrderDetail queryOrderDetail = JSONObject.parseObject(resp, QueryOrderDetail.class);
            return Optional.of(queryOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Place spot order
     *
     * @param spotClient  constructed client
     * @param tradingPair trading pair, e.g. BTCUSDT
     * @param direction   BUY or SELL
     * @param type        strategy, MARKET, STOP_LOSS, TAKE_PROFIT...
     * @param quantity    specific quantity for buying or selling
     * @return opt
     */
    public static Optional<OrderDetail> placeSpotOrder(SpotClientImpl spotClient, String tradingPair, String direction, String type, String quantity) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("symbol", tradingPair);
        reqMap.put("side", direction);
        reqMap.put("type", type);
        reqMap.put("quantity", quantity);
        try {
            String resp = spotClient.createMarket().depth(reqMap);
            OrderDetail orderDetail = JSONObject.parseObject(resp, OrderDetail.class);
            return Optional.of(orderDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


}
