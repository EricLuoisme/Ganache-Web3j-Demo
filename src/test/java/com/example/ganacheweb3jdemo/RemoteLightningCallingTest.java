package com.example.ganacheweb3jdemo;

import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


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

//    private final static String FILE_BASE_PATH = "/Users/pundix2022/Desktop/开发项目/LightningNetworkRelated/";
//    private final static String CERT_PATH = FILE_BASE_PATH + "tls.cert";
//    private final static String MACAROON_PATH = FILE_BASE_PATH + "admin.macaroon";

    private final static String FILE_BASE_PATH = "D:\\lightning\\";
    private final static String CERT_PATH = FILE_BASE_PATH + "tls.cert";
    private final static String MACAROON_PATH = FILE_BASE_PATH + "admin.macaroon";


    // ************************************************** LND Nodes *********************************************************
    @Test
    public void connectionTest() throws IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final Certificate certificate = cf.generateCertificate(new FileInputStream(CERT_PATH));

        String macaroonHexStr = Hex.encodeHexString(Files.readAllBytes(Paths.get(MACAROON_PATH)));

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://" + NODE_IP + ":" + NODE_REST_PORT + "/v1/getinfo").newBuilder();
        Request requestGet = new Request.Builder()
                .url(urlBuilder.build().toString())
                .header("Grpc-Metadata-macaroon", macaroonHexStr)
                .build();

        // create suitable trust manager & key manager for https request
        SSLContext sslContext = SSLContext.getInstance("TLS");
        X509TrustManager x509TrustManager = generateTrustManagerByFile(certificate);
        X509KeyManager x509KeyManager = generateKeyManagerByFile(certificate);
        sslContext.init(
                new KeyManager[]{x509KeyManager},
                new TrustManager[]{x509TrustManager},
                new java.security.SecureRandom());


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager)
                .build();
        Call call = okHttpClient.newCall(requestGet);

        ResponseBody body = call.execute().body();
        if (null != body) {
            System.out.println();
            System.out.println(body.string());
            System.out.println();
        }

    }

    @NotNull
    private X509KeyManager generateKeyManagerByFile(Certificate certificate) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(null, null);

        X509KeyManager manager = (X509KeyManager) kmf.getKeyManagers()[0];
        return new X509KeyManager() {
            @Override
            public String[] getClientAliases(String keyType, Principal[] issuers) {
                return manager.getServerAliases(keyType, issuers);
            }

            @Override
            public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
                return manager.chooseClientAlias(keyType, issuers, socket);
            }

            @Override
            public String[] getServerAliases(String keyType, Principal[] issuers) {
                return manager.getServerAliases(keyType, issuers);
            }

            @Override
            public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
                return manager.chooseServerAlias(keyType, issuers, socket);
            }

            @Override
            public X509Certificate[] getCertificateChain(String alias) {
                // Here to get the appropriate file
                return new X509Certificate[] {(X509Certificate) certificate};
            }

            @Override
            public PrivateKey getPrivateKey(String alias) {
                return null;
            }
        };
    }

    @NotNull
    private X509TrustManager generateTrustManagerByFile(Certificate certificate) {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                try {
                    certificate.verify(chain[0].getPublicKey());
                } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
                    System.out.println("Client Illegal");
                    e.printStackTrace();
                }
                System.out.println("Client Trusted");
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                try {
                    certificate.verify(chain[0].getPublicKey());
                } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
                    System.out.println("Server Illegal");
                    e.printStackTrace();
                }
                System.out.println("Server Trusted");
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
    }


    @Test
    public KeyStore readKeyStore() throws KeyStoreException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        // get user password and file input stream
        char[] password = "123456".toCharArray();

        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(FILE_BASE_PATH + "/lnd_keystore");
            ks.load(fis, password);
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return ks;
    }

}
