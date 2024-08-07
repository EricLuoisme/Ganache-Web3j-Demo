package com.own.third.api.telegram;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.TrustAllX509CertManager;
import net.steppschuh.markdowngenerator.table.Table;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TelegramBotTest {


    private static final String AUTH_TOKEN = "";

    private static final String BASE_URL = "https://api.telegram.org/bot";

    private static final String CHAT_ID = "";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();

    private static final ObjectMapper om = new ObjectMapper();

    private final MediaType mediaType = MediaType.parse("application/json");

    @Test
    public void getUpdateTest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + AUTH_TOKEN).newBuilder();
        urlBuilder.addPathSegment("getUpdates");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String respStr = response.body().string();
            JSONObject jsonObject = JSON.parseObject(respStr);
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void sendMsgTest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + AUTH_TOKEN).newBuilder();
        urlBuilder.addPathSegment("sendMessage");
        String url = urlBuilder.build().toString();

//        String text = "*bold \\*text*\n" +
//                "_italic \\*text_\n" +
//                "__underline__\n" +
//                "~strikethrough~\n" +
//                "||spoiler||\n" +
//                "*bold _italic bold ~italic bold strikethrough ||italic bold strikethrough spoiler||~ __underline italic bold___ bold*\n" +
//                "[inline URL](http://www.example.com/)\n" +
//                "[inline mention of a user](tg://user?id=123456789)\n" +
//                "![\uD83D\uDC4D](tg://emoji?id=5368324170671202286)\n" +
//                "`inline fixed-width code`\n" +
//                "```\n" +
//                "pre-formatted fixed-width code block\n" +
//                "```\n" +
//                "```python\n" +
//                "pre-formatted fixed-width code block written in the Python programming language\n" +
//                "```";

//        String text = "⚠\uFE0F*BALANCE ALERT*\n" +
//                "Remaining BTC Balance less than 0\\.0005\n\n" +
//                "_Service_:   dec\\-the\\-service\n" +
//                "_Time_:      20220902 12:23:23 UTC\\+8" +
//                "" +
//                "```AccountNo \n123423xxxxxxx1324234" +
//                "```";

//        String text = "*Money Flow Checking*\n\n" +
//                "```BTCUSDT \n" +
//                "   DATE      START       BTC         USDT\n" +
//                "2024\\-01\\-11   05\\:55\\:47  \\-0\\.00321   \\+124\\.46713429\n" +
//                "2024\\-01\\-11   05\\:55\\:47  \\-0\\.00321   \\+124\\.46713429\n" +
//                "```";

        final Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
                .withRowLimit(2)
                .addRow("Date", "START", "END", "BTC", "USDT");
        tableBuilder.addRow("2024-01-11", "05:55:47", "06:55:47", "-0.00321", "+124.46713429");
        String text = tableBuilder.build().toString();

        text = "*Money Flow Checking*\n\n" +
                "```BTCUSDT \n" +
                text +
                "\n```";


        JSONObject reqJson = new JSONObject();
        reqJson.put("chat_id", CHAT_ID);
        reqJson.put("text", text);
        reqJson.put("parse_mode", "Markdown");
        RequestBody body = RequestBody.create(reqJson.toJSONString(), mediaType);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String respStr = response.body().string();
            JSONObject jsonObject = JSON.parseObject(respStr);
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
