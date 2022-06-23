package com.example.web3j.combination.crawler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roylic
 * 2022/6/13
 */
public class DappComReqCrawler {

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();


    public static void main(String[] args) throws IOException {


        List<DappComDetail> resultList = new LinkedList<>();

        // try 25 pages first
        for (int i = 0; i < 25; i++) {
            // building url
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.dapp.com/api/ranking/dapp/?chain=0&page=" + (i + 1) + "&sort=usd_24h").newBuilder();
            String url = urlBuilder
                    .build()
                    .toString();

            // building request
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = okHttpClient.newCall(request);

            ResponseBody body = call.execute().body();
            if (null != body) {
                JSONObject pageResult = JSONObject.parseObject(body.string());
                JSONArray results = pageResult.getJSONArray("results");
                results.forEach(obj -> {

                    JSONObject jsonObject = (JSONObject) obj;
                    DappComDetail dappComDetail = JSONObject.parseObject(jsonObject.toJSONString(), DappComDetail.class);

                    JSONObject dapp = jsonObject.getJSONObject("dapp");
                    dappComDetail.setName((String) dapp.get("name"));
                    dappComDetail.setDescription((String) dapp.get("abstract"));

                    JSONObject categoryObj = dapp.getJSONObject("category");
                    dappComDetail.setCategory((String) categoryObj.get("name"));

                    StringBuilder chains = new StringBuilder();
                    JSONArray chainsArr = dapp.getJSONArray("chains");
                    chainsArr.forEach(chain -> {
                        JSONObject chainObj = (JSONObject) chain;
                        chains.append(chainObj.get("name"))
                                .append(",");
                    });
                    dappComDetail.setChains(chains.toString());

                    dappComDetail.setSocial_signal((Integer) dapp.get("social_signal"));
                    dappComDetail.setSocial_signal_gr((BigDecimal) dapp.get("social_signal_gr"));

                    resultList.add(dappComDetail);
                });
            }
            System.out.println(">>> finished page:" + (i + 1));
        }

        outputAsFile(resultList);

        System.out.println("<<< all finished");
    }

    private static void outputAsFile(List<DappComDetail> dappComDetails) throws IOException {
        System.out.println(">>> start formatting file");

        String fileName = "dapp_com_first_25_page.xlsx";
        EasyExcel.write(fileName, DappComDetail.class)
                        .sheet("dappCom")
                                .doWrite(dappComDetails);

        System.out.println("<<< finished formatting file");
    }


}
