package com.example.ganacheweb3jdemo.crawler;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 解析Python爬虫文件
 *
 * @author Roylic
 * 2022/6/7
 */
public class ParseDappRadar {


    private static final String FILE_PATH = "/Users/pundix2022/Juypter Working Env/";


    public static void main(String[] args) {
        parsingProcess();
    }

    private static void parsingProcess() {
        try {
            String fileStr = FileUtils.readFileToString(new File(FILE_PATH + "rank_top_all_2022-06-07.json"), Charset.defaultCharset());
            ObjectMapper om = new ObjectMapper();
            String str = om.writerWithDefaultPrettyPrinter().writeValueAsString(JSON.parse(fileStr));
            System.out.println(str);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
