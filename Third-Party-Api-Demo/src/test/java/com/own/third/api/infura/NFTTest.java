package com.own.third.api.infura;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.third.api.TrustAllX509CertManager;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Infura not decoded all NFT, thus, some 'name', 'description', 'imgUrl' are result in null
 *
 * @author Roylic
 * 2023/7/24
 */
public class NFTTest {

    private final ObjectMapper om = new ObjectMapper();

    private static final String NFT_REQUEST_URL = "https://nft.api.infura.io/networks/%d/accounts/%s/assets/nfts";

    private static final String CREDENTIAL = Credentials.basic("", "");

    private static final String COMPACT_CREDENTIAL = "";


    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();


    @Test
    public void getAllNftOwnedByAddress() throws IOException {

        String owner = "";
        Long chainId = 97L;

        String cursor = "eyJhbGciOiJIUzI1NiJ9.ZXlKaGJHY2lPaUpJVXpJMU5pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmpkWE4wYjIxUVlYSmhiWE1pT25zaWQyRnNiR1YwUVdSa2NtVnpjeUk2SWpCNE56ZG1NakF5TWpVek1qQXdPV00xWldJMFl6WmpOekJtTXprMVpHVmhZV0UzT1RNME9ERmlZeUo5TENKclpYbHpJanBiSWpFMk5ESTFNRGt6TURFdU5UZzFJbDBzSW5kb1pYSmxJanA3SW05M2JtVnlYMjltSWpvaU1IZzNOMll5TURJeU5UTXlNREE1WXpWbFlqUmpObU0zTUdZek9UVmtaV0ZoWVRjNU16UTRNV0pqSW4wc0lteHBiV2wwSWpveE1EQXNJbTltWm5ObGRDSTZNQ3dpYjNKa1pYSWlPbHRkTENKa2FYTmhZbXhsWDNSdmRHRnNJanBtWVd4elpTd2lkRzkwWVd3aU9qUXpOU3dpY0dGblpTSTZNU3dpZEdGcGJFOW1abk5sZENJNk1Td2lhV0YwSWpveE5qa3dNVGsxTmpRd2ZRLkpLamJ6UTFTbUZXNzFaRHlkel9tLWt6VndWSjB2UXIxRS10NG5DTXg5UWc.wOgo9rWXDLADbydParU7L-cINbhqx5Z0VeuaJinmfDA";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(String.format(NFT_REQUEST_URL, chainId, owner)).newBuilder();
        urlBuilder.addQueryParameter("cursor", cursor);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(COMPACT_CREDENTIAL.getBytes(StandardCharsets.UTF_8)))
                .build();

        Response response = okHttpClient.newCall(request).execute();
        NftResponse nftResponse = JSON.parseObject(response.body().string(), NftResponse.class);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(nftResponse));
        System.out.println();
    }

}
