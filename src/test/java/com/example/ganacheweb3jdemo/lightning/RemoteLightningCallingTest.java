package com.example.ganacheweb3jdemo.lightning;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.proto.LightningGrpc;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.ValidationException;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * cert: https://www.baeldung.com/okhttp-self-signed-cert
 * trust: https://github.com/lightningnetwork/lnd/blob/master/docs/grpc/java.md
 * hostname: https://stackoverflow.com/questions/31917988/okhttp-javax-net-ssl-sslpeerunverifiedexception-hostname-domain-com-not-verifie
 *
 * @author Roylic
 * @date 2022/4/21
 */
public class RemoteLightningCallingTest {

    private final static String NODE_IP = "18.139.32.218";
    private final static int NODE_GRPC_PORT = 10009;
    private final static int NODE_REST_PORT = 8080;

    private final static String FILE_BASE_PATH = "/Users/pundix2022/Desktop/开发项目/LightningNetworkRelated/";
    private final static String CERT_PATH = FILE_BASE_PATH + "tls.cert";
    private final static String MACAROON_PATH = FILE_BASE_PATH + "admin.macaroon";

//    private final static String FILE_BASE_PATH = "D:\\lightning\\";
//    private final static String CERT_PATH = FILE_BASE_PATH + "tls.cert";
//    private final static String MACAROON_PATH = FILE_BASE_PATH + "admin.macaroon";


    // ************************************************** LND Nodes With RESTFul *********************************************************
    @Test
    public void connectionTest() throws IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {

        // get certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // generate certificate instance from input file stream, this certificate would be used in verifying response
        final Certificate certificate = cf.generateCertificate(new FileInputStream(CERT_PATH));

        // lnd only receive hex string without '0x' as header, thus, if we using Numeric.toHexString, must cut '0x'
        String macaroonHexStr = Hex.encodeHexString(Files.readAllBytes(Paths.get(MACAROON_PATH)));

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://" + NODE_IP + ":" + NODE_REST_PORT + "/v1/channels").newBuilder();
        Request requestGet = new Request.Builder()
                .url(urlBuilder.build().toString())
                // lnd node receive Macaroon Hex as Header below
                .header("Grpc-Metadata-macaroon", macaroonHexStr)
                .build();

        // ssl context would be specific using TLS
        SSLContext sslContext = SSLContext.getInstance("TLS");
        // create suitable trust manager & key manager for https request
        X509TrustManager x509TrustManager = generateTrustManagerByFile(certificate);
        X509KeyManager x509KeyManager = generateKeyManagerByFile(certificate);
        sslContext.init(
                new KeyManager[]{x509KeyManager},
                new TrustManager[]{x509TrustManager},
                new java.security.SecureRandom());

        // if we requesting by ip directly, must create suitable hostname verifier
        HostnameVerifier hostnameVerifier = generateHostnameVerifier();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // set socket factory
                .sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager)
                // set hostname verifier
                .hostnameVerifier(hostnameVerifier)
                .build();
        Call call = okHttpClient.newCall(requestGet);

        ResponseBody body = call.execute().body();
        if (null != body) {
            System.out.println();
            System.out.println(body.string());
            System.out.println();
        }

    }

    /**
     * Key Manager specific on requesting to the remote server
     *
     * @param certificate tls.cert
     * @return X509KeyManager
     */
    @NotNull
    private X509KeyManager generateKeyManagerByFile(Certificate certificate)
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {

        // get the key manager from jvm
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        // must initial it with this line
        kmf.init(null, null);

        // get the first manager and add it's related stuff into the one we wanna generate
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

            /**
             * For this specific request, we know out destination,
             * thus, for all socket from this okhttp instance,
             * we can just return this only certificate
             */
            @Override
            public X509Certificate[] getCertificateChain(String alias) {
                // Here to get the appropriate file
                return new X509Certificate[]{(X509Certificate) certificate};
            }

            @Override
            public PrivateKey getPrivateKey(String alias) {
                // not realy exchange the key to the server
                return null;
            }
        };
    }


    /**
     * Trust Manager specific on the responding from the server
     *
     * @param certificate tls.cert
     * @return X509TrustManager
     */
    @NotNull
    private X509TrustManager generateTrustManagerByFile(Certificate certificate) {
        return new X509TrustManager() {
            /**
             * verify if client connect to us
             */
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                try {
                    certificate.verify(chain[0].getPublicKey());
                } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException
                        | NoSuchProviderException | SignatureException e) {
                    System.out.println("Client Illegal");
                    e.printStackTrace();
                }
                System.out.println("Client Trusted");
            }

            /**
             * verify server's response
             */
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                try {
                    certificate.verify(chain[0].getPublicKey());
                } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException
                        | NoSuchProviderException | SignatureException e) {
                    System.out.println("Server Illegal");
                    e.printStackTrace();
                }
                System.out.println("Server Trusted");
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{(X509Certificate) certificate};
            }
        };
    }

    /**
     * new host name verifier that accept all host
     *
     * @return HostnameVerifier
     */
    @NotNull
    private HostnameVerifier generateHostnameVerifier() {
        // because we are using the ip to call the server,
        // for host name verification, we just true for all
        // or else get block...
        return (hostname, session) -> true;
    }


    // ************************************************** LND Nodes With Grpc *********************************************************

    @Test
    public void grpc_connection() throws StatusException, SSLException, ValidationException, NoSuchAlgorithmException, KeyManagementException, CertificateException, FileNotFoundException, UnrecoverableKeyException, KeyStoreException {
        // Alice
//        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
//                NODE_IP,
//                NODE_GRPC_PORT,
//                new File(CERT_PATH),
//                new File(MACAROON_PATH));

        // get certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // generate certificate instance from input file stream, this certificate would be used in verifying response
        final Certificate certificate = cf.generateCertificate(new FileInputStream(CERT_PATH));

        // ssl context would be specific using TLS
//        SSLContext sslContext = SSLContext.getInstance("TLS");
        // create suitable trust manager & key manager for https request
        X509TrustManager x509TrustManager = generateTrustManagerByFile(certificate);
//        X509KeyManager x509KeyManager = generateKeyManagerByFile(certificate);
//        sslContext.init(
//                new KeyManager[]{x509KeyManager},
//                new TrustManager[]{x509TrustManager},
//                new java.security.SecureRandom());


        SslContext sslRealContext = GrpcSslContexts
                .configure(SslContextBuilder.forClient(), SslProvider.OPENSSL)
                .trustManager(x509TrustManager)
                .build();


        ManagedChannel channel = NettyChannelBuilder.forAddress(NODE_IP, NODE_GRPC_PORT)
                .sslContext(sslRealContext)
                .build();

        LightningGrpc.LightningBlockingStub stub = LightningGrpc.newBlockingStub(channel);
        LightningApi.ListChannelsRequest.Builder reqBuilder = LightningApi.ListChannelsRequest.newBuilder();
        LightningApi.ListChannelsResponse listChannelsResponse = stub.listChannels(reqBuilder.build());
        System.out.println(listChannelsResponse.toString());
    }

}
