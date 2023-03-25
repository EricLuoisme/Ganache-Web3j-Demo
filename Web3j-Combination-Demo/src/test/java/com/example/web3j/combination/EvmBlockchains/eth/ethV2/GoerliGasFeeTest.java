package com.example.web3j.combination.EvmBlockchains.eth.ethV2;

import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 查询GasFee
 *
 * @author Roylic
 * 2022/10/27
 */
public class GoerliGasFeeTest {

    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();
    private static final MediaType mediaType = MediaType.parse("application/json");
    private static final String REQUEST_TEMPLATE = "{\"query\":\"\\n            query ($network: EthereumNetwork!,\\n                  $dateFormat: String!,\\n\\n                  $from: ISO8601DateTime,\\n                  $till: ISO8601DateTime){\\n                    ethereum(network: $network ){\\n                      transactions(options:{asc: \\\"date.date\\\"}, date: {\\n                        since: $from\\n                        till: $till}\\n\\n                      ) {\\n                        date: date{\\n                          date(format: $dateFormat)\\n                        }\\n                        gasPrice\\n                        gasValue\\n                        average: gasValue(calculate: average )\\n                        maxGasPrice: gasPrice(calculate: maximum)\\n                        medianGasPrice: gasPrice(calculate: median)\\n                      }\\n                    }\\n                  }\",\"variables\":\"{\\\"limit\\\":10,\\\"offset\\\":0,\\\"network\\\":\\\"goerli\\\",\\\"from\\\":\\\"%s\\\",\\\"till\\\":\\\"%sT23:59:59\\\",\\\"dateFormat\\\":\\\"%%Y-%%m-%%d\\\"}\"}";


    @Test
    public void querying() throws IOException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String usingDate = dtf.format(now);
        String requestStr = String.format(REQUEST_TEMPLATE, usingDate, usingDate);

        RequestBody body = RequestBody.create(requestStr, mediaType);
        Request request = new Request.Builder()
                .url("https://graphql.bitquery.io")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-API-KEY", "BQYUQ8HSyhhY0c7h2D5Pz1E9vB4a2WtL")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

}
