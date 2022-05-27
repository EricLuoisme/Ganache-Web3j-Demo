package com.example.ganacheweb3jdemo;

import okhttp3.*;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;


/**
 * 尝试进行RPC解析测试 - Remote
 *
 * @author Roylic
 * @date 2022/4/21
 */
public class RemoteLightningCallingTest {

    private final static String NODE_IP = "18.139.32.218";
    private final static int NODE_GRPC_PORT = 10009;
    private final static int NODE_REST_PORT = 9735;
    private final static String FILE_BASE_PATH = "/Users/pundix2022/Desktop/开发项目/LightningNetworkRelated/";
    private final static String CERT_PATH = FILE_BASE_PATH + "tls.cert";
    private final static String MACAROON_PATH = FILE_BASE_PATH + "admin.macaroon";


    // ************************************************** LND Nodes *********************************************************
    @Test
    public void connectionTest() throws IOException, CertificateException {

        // By Rest
        // toBase64String already contains calling the encode function
//        byte[] macaroonBytes = Files.readAllBytes(Paths.get(MACAROON_PATH));
//        String macaroonBase64 = Base64.toBase64String(macaroonBytes);
//
//
//        byte[] bytes = Files.readAllBytes(Paths.get(CERT_PATH));
//        String s = new String(bytes);
//        System.out.println("\n Cert \n");
//        System.out.println(s);
//
//        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        Certificate certificate = cf.generateCertificate(new FileInputStream(CERT_PATH));
//
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://" + NODE_IP + ":" + NODE_REST_PORT + "/v1/getinfo").newBuilder();
//        String url = urlBuilder
//                .build()
//                .toString();
//
//        Request requestGet = new Request.Builder()
//                .url(url)
//                .header("Grpc-Metadata-macaroon", macaroonBase64)
//                .build();
//
//        OkHttpClient okHttpClient = OkHttpClientFactory.genOkHttpClient(CERT_PATH);
//        Call call = okHttpClient.newCall(requestGet);
//
//        ResponseBody body = call.execute().body();
//        if (null != body) {
//            System.out.println();
//            System.out.println(body.string());
//            System.out.println();
//        }


    }





}
