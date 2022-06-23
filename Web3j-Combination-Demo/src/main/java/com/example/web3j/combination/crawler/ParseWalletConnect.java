package com.example.web3j.combination.crawler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 解析Python爬虫文件
 *
 * @author Roylic
 * 2022/6/7
 */
public class ParseWalletConnect {


    private static final String FILE_PATH = "/Users/pundix2022/Juypter Working Env/";
    private static final String FILE_NAME = "wallet_connect_page_";
    private static final String FILE_TYPE = ".json";


    public static void main(String[] args) throws InterruptedException, IOException {

        // parse wallet connect json file
        Map<String, String> theMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();

        for (int i = 0; i < 12; i++) {
            String fileStr = FileUtils.readFileToString(new File(FILE_PATH + FILE_NAME + i + FILE_TYPE), Charset.defaultCharset());
            JSONObject fileJson = JSONObject.parseObject(fileStr);
            JSONObject pageProps = fileJson.getJSONObject("pageProps");

            List<WalletConnectDetail> walletDetailFile = i == 0 ?
                    JSON.parseArray(pageProps.getJSONArray("dapps").toJSONString(), WalletConnectDetail.class)
                    : JSON.parseArray(pageProps.getJSONArray("listings").toJSONString(), WalletConnectDetail.class);

            walletDetailFile.forEach(detail -> {
                theMap.put(detail.getName().toLowerCase(Locale.ROOT), detail.getName().toLowerCase(Locale.ROOT));
            });
        }


        // parse dappradar json file

        String fileStr = FileUtils.readFileToString(new File("/Users/pundix2022/Downloads/DappRadar Dapp 1-255.json"), Charset.defaultCharset());
        // obj
        List<JSONObject> dappArr = JSON.parseArray(fileStr, JSONObject.class);
        dappArr.forEach(dapp -> {
            String title = dapp.get("Title").toString().toLowerCase(Locale.ROOT);
            if (theMap.containsKey(title)) {
                innerMap.put(title, title);
            }
        });

        // pretty printf
        ObjectMapper om = new ObjectMapper();
        String str = om.writerWithDefaultPrettyPrinter().writeValueAsString(JSON.parse(fileStr));
        System.out.println(str);
    }

    private static void exportAsExcel() throws IOException {
        // parse json files
        List<WalletConnectDetail> walletDetails = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            String fileStr = FileUtils.readFileToString(new File(FILE_PATH + FILE_NAME + i + FILE_TYPE), Charset.defaultCharset());
            JSONObject fileJson = JSONObject.parseObject(fileStr);
            JSONObject pageProps = fileJson.getJSONObject("pageProps");

            List<WalletConnectDetail> walletDetailFile = i == 0 ?
                    JSON.parseArray(pageProps.getJSONArray("dapps").toJSONString(), WalletConnectDetail.class)
                    : JSON.parseArray(pageProps.getJSONArray("listings").toJSONString(), WalletConnectDetail.class);

            walletDetails.addAll(walletDetailFile);
        }

        // output as excel
        Map<String, String> excelMap = new LinkedHashMap<>();
        excelMap.put("category", "dapp类别");
        excelMap.put("id", "dapp唯一id");
        excelMap.put("homepage", "dapp主页");
        excelMap.put("name", "dapp名称");
        excelMap.put("image", "dapp图片");

        Collection<Object> dataset = new ArrayList<>(walletDetails);
        File f = new File("wallet_connect.xls");
        OutputStream out = new FileOutputStream(f);

        ExcelUtil.exportExcel(excelMap, dataset, out);
        out.close();
    }


}
