package com.example.ganacheweb3jdemo.crawler;

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

        String fileStr_07 = FileUtils.readFileToString(new File(BASE_FILE_PATH + "rank_top_all_2022-06-07.json"), Charset.defaultCharset());
        String fileStr_08 = FileUtils.readFileToString(new File(BASE_FILE_PATH + "rank_top_all_2022-06-08.json"), Charset.defaultCharset());

        JSONObject file_07 = JSONObject.parseObject(fileStr_07);
        JSONObject file_08 = JSONObject.parseObject(fileStr_08);

        JSONArray dapps_07 = file_07.getJSONArray("dapps");
        JSONArray dapps_08 = file_08.getJSONArray("dapps");

        List<DappDetail> dappDetails_07 = JSON.parseArray(dapps_07.toJSONString(), DappDetail.class);
        List<DappDetail> dappDetails_08 = JSON.parseArray(dapps_08.toJSONString(), DappDetail.class);

        List<DappDetail> main_page_sort = dappDetails_08.stream()
                .sorted((o1, o2) -> o2.getStatistic().getUserActivity() - o1.getStatistic().getUserActivity())
                .collect(Collectors.toList());

        List<DappDetail> first_9 = main_page_sort.subList(0, 9);
        List<DappDetail> second_9 = main_page_sort.subList(9, 18);

        // remote only accept 9 request as a bucket
        first_9.forEach(dapp -> {
            try {
                URL url = new URL(dapp.getDeepLink());
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(7000);
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
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        TimeUnit.SECONDS.sleep(5);
        System.out.print("\nStart Second Loop\n");

        // remote only accept 9 request as a bucket
        second_9.forEach(dapp -> {
            try {
                URL url = new URL(dapp.getDeepLink());
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(7000);
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
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });


        System.out.println();
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
