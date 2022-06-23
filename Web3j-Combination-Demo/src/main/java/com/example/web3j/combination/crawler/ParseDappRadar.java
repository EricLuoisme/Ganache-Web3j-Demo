package com.example.web3j.combination.crawler;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 解析Python爬虫文件
 *
 * @author Roylic
 * 2022/6/7
 */
public class ParseDappRadar {


    private static final String SCRIPT_PATH = "/Users/pundix2022/PycharmProjects/dapps-scraping/own_dappradar/save_dappradar_file.py";


    public static void main(String[] args) throws InterruptedException {

        String allFileNames = crawlingData("python " + SCRIPT_PATH);
        String[] split = allFileNames.split(",");
        for (String sinFile : split) {
            parsingProcess(sinFile);
            System.out.println();
            System.out.println();
            System.out.println();
        }

//        parsingProcess("/Users/pundix2022/Juypter Working Env/rank_top_all_2022-06-13.json");

//        parsingProcess("/Users/pundix2022/Downloads/DappRadar Dapp 1-255.json");

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
}
