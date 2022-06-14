package com.example.ganacheweb3jdemo.crawler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 解析Python爬虫文件
 *
 * @author Roylic
 * 2022/6/7
 */
public class ParseDappRadarTest {


    private static final String SCRIPT_PATH = "/Users/pundix2022/PycharmProjects/dapps-scraping/own_dappradar/save_dappradar_file.py";
    private static final String BASE_FILE_PATH = "/Users/pundix2022/Juypter Working Env/";


    @Test
    public void crawling() {
        crawlingData("python " + SCRIPT_PATH);
    }

    @Test
    public void decoding() throws IOException, InterruptedException {

        String fileStr = FileUtils.readFileToString(new File(BASE_FILE_PATH + "rank_top_all_2022-06-13.json"), Charset.defaultCharset());

        JSONObject file = JSONObject.parseObject(fileStr);

        JSONArray dappsArr = file.getJSONArray("dapps");


        List<DappRadarDetail> dappDetails = new LinkedList<>();

        dappsArr.forEach(dapp -> {
            JSONObject dappObj = (JSONObject) dapp;
            DappRadarDetail dappRadarDetail = JSONObject.parseObject(dappObj.toJSONString(), DappRadarDetail.class);
            dappRadarDetail.setIsNew(dappObj.getBoolean("new"));
            // concat protocols
            StringBuilder supportProtocols = new StringBuilder();
            dappRadarDetail.getProtocols().forEach(proto -> supportProtocols.append(proto).append(","));
            dappRadarDetail.setSupportProtocols(supportProtocols.toString());

            StringBuilder activeSupportProtocols = new StringBuilder();
            dappRadarDetail.getActiveProtocols().forEach(proto -> activeSupportProtocols.append(proto).append(","));
            dappRadarDetail.setActiveSupportProtocols(activeSupportProtocols.toString());

            // fill statistics
            JSONObject statistic = dappObj.getJSONObject("statistic");
            dappRadarDetail.setBalance(statistic.getInteger("balance"));
            dappRadarDetail.setBalanceInFiat(statistic.getInteger("balanceInFiat"));
            dappRadarDetail.setGraph(statistic.getString("graph"));
            dappRadarDetail.setExchangeRate(statistic.getInteger("exchangeRate"));
            dappRadarDetail.setCurrencyName(statistic.getString("currencyName"));
            dappRadarDetail.setTransactionCount(statistic.getInteger("transactionCount"));
            dappRadarDetail.setUserActivity(statistic.getInteger("userActivity"));
            dappRadarDetail.setVolumeInFiat(statistic.getInteger("volumeInFiat"));
            dappRadarDetail.setTotalVolumeInFiat(statistic.getBigDecimal("totalVolumeInFiat"));
            dappRadarDetail.setTotalVolumeChangeInFiat(statistic.getBigDecimal("totalVolumeChangeInFiat"));

            // fill changes
            JSONObject changes = statistic.getJSONObject("changes");
            JSONObject dau = changes.getJSONObject("dau");
            JSONObject volume = changes.getJSONObject("volume");
            JSONObject tx = changes.getJSONObject("tx");
            JSONObject tokenVolume = changes.getJSONObject("tokenVolume");
            JSONObject totalVolume = changes.getJSONObject("totalVolume");
            JSONObject totalBalance = changes.getJSONObject("totalBalance");

            dappRadarDetail.setDauLabel(dau.getString("label"));
            dappRadarDetail.setDauStatus(dau.getString("status"));
            dappRadarDetail.setVolumeLabel(volume.getString("label"));
            dappRadarDetail.setVolumeStatus(volume.getString("status"));
            dappRadarDetail.setTxLabel(tx.getString("label"));
            dappRadarDetail.setTxStatus(tx.getString("status"));
            dappRadarDetail.setTokenVolumeLabel(tokenVolume.getString("label"));
            dappRadarDetail.setTokenVolumeStatus(tokenVolume.getString("status"));
            dappRadarDetail.setTotalVolumeLabel(totalVolume.getString("label"));
            dappRadarDetail.setTotalVolumeStatus(totalBalance.getString("status"));
            dappRadarDetail.setTotalBalanceLabel(totalBalance.getString("label"));
            dappRadarDetail.setTotalBalanceStatus(totalBalance.getString("status"));


            dappDetails.add(dappRadarDetail);
        });

        // correct the order
        List<DappRadarDetail> main_page_sort = dappDetails.stream()
                .sorted((o1, o2) -> o2.getUserActivity() - o1.getUserActivity())
                .collect(Collectors.toList());

        // replace real url
        replaceForRealUrl(main_page_sort);

        // export to excel
        outputAsFile(main_page_sort);
    }

    private static void outputAsFile(List<DappRadarDetail> dappRadarDetails) throws IOException {
        System.out.println(">>> start formatting file");

        String fileName = "dapp_radar_first_25_dapp.xlsx";
        EasyExcel.write(fileName, DappRadarDetail.class)
                .sheet("dappradar")
                .doWrite(dappRadarDetails);

        System.out.println("<<< finished formatting file");
    }


    private void replaceForRealUrl(List<DappRadarDetail> main_page_sort) throws InterruptedException {
        // calling for real url
        main_page_sort.forEach(dapp -> {
            if (dapp.getDeepLink().contains("dappradar")) {
                try {
                    URL url = new URL(dapp.getDeepLink());
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    // not set properties would get access denied from radar
                    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                    conn.addRequestProperty("User-Agent", "Mozilla");
                    conn.addRequestProperty("Referer", "google.com");
                    // trust every one
                    conn.setHostnameVerifier((s, sslSession) -> true);

                    // normally, 3xx is redirect, but radar response 200 and redirect it
                    int status = conn.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == status) {
                        System.out.println("Redirected Url: " + conn.getURL().toString());
                        dapp.setDeepLink(conn.getURL().toString());
                        conn.disconnect();
                    }
                    TimeUnit.MILLISECONDS.sleep(3000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void getRedirectUrl() throws IOException {
        String urlPath = "https://dappradar.com/deeplink/4600";

        URL obj = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setReadTimeout(5000);
        // not set properties would get access denied from radar
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");

        // normally, 3xx is redirect, but radar response 200 and redirect it
        int status = conn.getResponseCode();
        if (HttpURLConnection.HTTP_OK == status) {
            System.out.println("Redirected Url: " + conn.getURL().toString());
        }
    }

    /**
     * Calling Python Script to collect data into json file
     */
    private static String crawlingData(String command) {
        String lineOutput = "";
        try {
            String str = "";
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((str = stdInput.readLine()) != null) {
                lineOutput = str;
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // the last line contains file names
        return lineOutput;
    }

    /**
     * Parsing json file
     */
    private static void parsingProcess(String filePath) {
        try {
            String fileStr = FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
            // obj
            Object parse = JSON.parse(fileStr);
            // pretty printf
            ObjectMapper om = new ObjectMapper();
            String str = om.writerWithDefaultPrettyPrinter().writeValueAsString(JSON.parse(fileStr));
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void consumeCommand(String command) {
        try {

            String s = null;

            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public static String getFinalURL(String url) throws IOException {
        try {

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");

            System.out.println("Request URL ... " + url);

            boolean redirect = false;

            // normally, 3xx is redirect, but radar response 200 and redirect it
            int status = conn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == status) {
                System.out.println("Redirected Url: " + conn.getURL().toString());
            }


            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            System.out.println("Response Code ... " + status);

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "google.com");

                System.out.println("Redirect to URL : " + newUrl);

            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer html = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }
            in.close();

            System.out.println("URL Content... \n" + html.toString());
            System.out.println("Done");

        } catch (Exception e) {
            e.printStackTrace();
        }


        return url;
    }
}
