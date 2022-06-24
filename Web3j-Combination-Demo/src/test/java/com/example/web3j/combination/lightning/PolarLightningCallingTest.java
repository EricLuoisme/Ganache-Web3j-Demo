package com.example.web3j.combination.lightning;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.web3j.okhttp.interceptor.ApplicationInterceptorImp;
import com.example.web3j.combination.web3j.okhttp.interceptor.LogInterceptorImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.*;
import org.web3j.utils.Numeric;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 尝试进行RPC解析测试
 * cert: https://www.baeldung.com/okhttp-self-signed-cert
 * trust: https://github.com/lightningnetwork/lnd/blob/master/docs/grpc/java.md
 *
 * @author Roylic
 * @date 2022/4/21
 */
public class PolarLightningCallingTest {

    private final static String POLAR_BASE_URL = "https://127.0.0.1:";
    private final static String POLAR_MACAROON_LOC = "/Users/pundix2022/.polar/networks/1/volumes/lnd";

    // Alice
    private final static int ALICE_GRPC_PORT = 10001;
    private final static String ALICE_REST_HOST = "https://127.0.0.1:8081";
    private final static String ALICE_CERT = POLAR_MACAROON_LOC + "/alice/tls.cert";
    private final static String ALICE_MACAROON = POLAR_MACAROON_LOC + "/alice/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ALICE_INVOICE_MACAROON = POLAR_MACAROON_LOC + "/alice/data/chain/bitcoin/regtest/invoice.macaroon";
    private final static String ALICE_PUB_KEY = "02c94f36b8574122ecf189a90fea84f02b8b80c21577f60499a2345ae9621c9fb4";

    // Erin
    private final static int ERIN_GRPC_PORT = 10005;
    private final static String ERIN_REST_HOST = "https://127.0.0.1:8085";
    private final static String ERIN_CERT = POLAR_MACAROON_LOC + "/erin/tls.cert";
    private final static String ERIN_MACAROON = POLAR_MACAROON_LOC + "/erin/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ERIN_PUB_KEY = "0283f7142dfd9fff02d5f68d139d4e6bb774e62c364c414128d85ae758bbf834a9";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_REST_HOST = "https://127.0.0.1:8084";
    private final static String DAVE_CERT = POLAR_MACAROON_LOC + "/dave/tls.cert";
    private final static String DAVE_MACAROON = POLAR_MACAROON_LOC + "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "03b042a9d98111696f7aa00815dd8c740a55afb452bf89de33a3001ce8a03f8f65";

    // Frank
    private final static int FRANK_GRPC_PORT = 10006;
    private final static String FRANK_REST_HOST = "https://127.0.0.1:8086";
    private final static String FRANK_CERT = POLAR_MACAROON_LOC + "/frank/tls.cert";
    private final static String FRANK_MACAROON = POLAR_MACAROON_LOC + "/frank/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String FRANK_PUB_KEY = "02ded0a280bdb225715901292bda186ef0d319c1cf3512b8626b7a72621c1aef46";

    // Bob - c-lightning
    private final static String BOB_REST_API = "http://127.0.0.1:8182/";
    private final static String BOB_MACAROON = "/Users/pundix2022/.polar/networks/1/volumes/c-lightning/bob/rest-api/access.macaroon";


    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(new ApplicationInterceptorImp())
            .addNetworkInterceptor(new LogInterceptorImp())
            .retryOnConnectionFailure(false)
            .build();


    // ************************************************** Grpc Request *********************************************************
    @Test
    public void LND_SyncChannelBalanceTest_Alice() throws IOException, StatusException, ValidationException {

        // Alice
        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));
        System.out.println("\nAlice\n");
//        System.out.println(synchronousLndAPI.listChannels(new ListChannelsRequest()).toJsonAsString(true));
//        System.out.println(synchronousLndAPI.feeReport().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.walletBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.listInvoices(new ListInvoiceRequest()).toJsonAsString(true));
//
//        System.out.println(synchronousLndAPI.listPayments(new ListPaymentsRequest()).toJsonAsString(true));

//        System.out.println(synchronousLndAPI.getChanInfo(148434069815297L));

//        SendRequest sendRequest = new SendRequest();
//        sendRequest.setPaymentRequest("lnbcrt500u1p3g73jspp5v8s6mywu3mlcz38y8axej6fus3y4ex8v3qfhfqxzw6md45ngznnsdqqcqzpgsp5ffxyh20dc4ztv6lwaj9k6pf7n9n5w705xzuuu6pu9mpangtnxfjs9qyyssq255dnth59h7dgu9nyh2ytu2j2r57npqy4w6esyv8tk5w3skj90uhgy9vrsqxy33sxq85aqqh59jq685anzxlm0w9fsk4y72jvls3eggq622hzh");
//
//        SendResponse sendResponse = synchronousLndAPI.sendPaymentSync(sendRequest);
//        System.out.println(sendResponse.toJsonAsString(true));


//        // routing fee query, would return a route array
        // the totalAmtMst would be Amt + Fee, in Msat (/1000 = rsatoshis)
    }

    @Test
    public void LND_SyncChannelBalanceTest_Erin() throws IOException, StatusException, ValidationException {
        // Erin
        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ERIN_GRPC_PORT,
                new File(ERIN_CERT),
                new File(ERIN_MACAROON));
        System.out.println("\nErin\n");
        System.out.println(synchronousLndAPI.listChannels(new ListChannelsRequest()).toJsonAsString(true));
        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));
        System.out.println(synchronousLndAPI.walletBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.getChanInfo(130841883770880L));
//        System.out.println(synchronousLndAPI.listInvoices(new ListInvoiceRequest()));
    }

    @Test
    public void LND_SyncChannelBalanceTest_Dave() throws IOException, StatusException, ValidationException {

        // Dave
        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                DAVE_GRPC_PORT,
                new File(DAVE_CERT),
                new File(DAVE_MACAROON));
        System.out.println("\nDave\n");
//        System.out.println(synchronousLndAPI.listChannels(new ListChannelsRequest()).toJsonAsString(true));
//        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.walletBalance().toJsonAsString(true));
        System.out.println(synchronousLndAPI.feeReport().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.getChanInfo(130841883770880L));

//        ListInvoiceRequest listInvoiceRequest = new ListInvoiceRequest();
//        listInvoiceRequest.setPendingOnly(true);
//        System.out.println(synchronousLndAPI.listInvoices(listInvoiceRequest));
    }

    @Test
    public void LND_OpenChannel_ByRpcAPI_Alice2Erin() throws StatusException, IOException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));

        OpenChannelRequest openChannelRequest = new OpenChannelRequest();
        openChannelRequest.setNodePubkey(Numeric.hexStringToByteArray(ERIN_PUB_KEY));
        // The number of SATs the wallet should commit to the channel
        openChannelRequest.setLocalFundingAmount(2_000_000L);
        // The number of SATs to push to the remote side as part of the initial commitment state
        openChannelRequest.setPushSat(5_000L);
        // The target number of blocks that the funding transaction should be confirmed by.
        openChannelRequest.setTargetConf(3);
        // Whether this channel should be private, not announced to the greater network.
        openChannelRequest.setPrivate(false);
        // The minimum number of confirmations each one of your outputs used for the funding transaction must satisfy.
        openChannelRequest.setMinConfs(5);
        // Whether unconfirmed outputs should be used as inputs for the funding transaction.
        openChannelRequest.setSpendUnconfirmed(false);


        // request
        Iterator<OpenStatusUpdate> result = synchronousLndAPI.openChannel(openChannelRequest);
        while (result.hasNext()) {
            System.out.println("Received Update: " + result.next().toJsonAsString(true));
        }

        // close stub
        synchronousLndAPI.close();
    }

    @Test
    public void LND_OpenChannel_ByRpcAPI_Erin2Dave() throws StatusException, IOException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ERIN_GRPC_PORT,
                new File(ERIN_CERT),
                new File(ERIN_MACAROON));

        OpenChannelRequest openChannelRequest = new OpenChannelRequest();
        openChannelRequest.setNodePubkey(Numeric.hexStringToByteArray(DAVE_PUB_KEY));
        // The number of SATs the wallet should commit to the channel
        openChannelRequest.setLocalFundingAmount(500_000L);
        // The number of SATs to push to the remote side as part of the initial commitment state
        openChannelRequest.setPushSat(50_000L);
        // The target number of blocks that the funding transaction should be confirmed by.
        openChannelRequest.setTargetConf(3);
        // Whether this channel should be private, not announced to the greater network.
        openChannelRequest.setPrivate(false);
        // The minimum number of confirmations each one of your outputs used for the funding transaction must satisfy.
        openChannelRequest.setMinConfs(5);
        // Whether unconfirmed outputs should be used as inputs for the funding transaction.
        openChannelRequest.setSpendUnconfirmed(false);


        // request
        Iterator<OpenStatusUpdate> result = synchronousLndAPI.openChannel(openChannelRequest);
        while (result.hasNext()) {
            System.out.println("Received Update: " + result.next().toJsonAsString(true));
        }

        // close stub
        synchronousLndAPI.close();
    }

    @Test
    public void LND_CreateInvoice_ByRpcAPI() throws StatusException, SSLException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                DAVE_GRPC_PORT,
                new File(DAVE_CERT),
                new File(DAVE_MACAROON));

        Invoice invoice = new Invoice();
        // 	An optional memo to attach along with the invoice. Used for record keeping purposes for the invoice's creator,
        // 	and will also be set in the description field of the encoded payment request if
        // 	the description_hash field is not being used.
        invoice.setMemo("Dave's invoice should be paid by Alice 0");
        // The value of this invoice in satoshis The fields value and value_msat are mutually exclusive.
        // if the value is smaller than node's routing policy, this invoice could not 'see' this route
        invoice.setValue(0L);

        AddInvoiceResponse addInvoiceResponse = synchronousLndAPI.addInvoice(invoice);
        System.out.println(addInvoiceResponse.toJsonAsString(true));
    }

    @Test
    public void LND_DecodePayReq_ByRpcAPI() throws StatusException, SSLException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));
        System.out.println("\nAlice\n");

        PayReq payReq = synchronousLndAPI.decodePayReq("lnbcrt500u1p3t253ypp5gt06trjpxldjtn9avwtm69h8j7gm55aue446y4puznhwu9mrkpysdqqcqzpgsp5645gphkn9445g2mmjpf5hl5mwntkpe8k6suj9fe8h94mgsv8pl9q9qyyssqspwhq0e6p7cd59vpmnhaj9jw5k27yyeedns3v5z7t8dl67849rg9ak9zdm8x0hhyqfny95narnwgyurm8d3s3v8u3xj08h6h984edvcp0jhs9z");
        System.out.println(payReq.toJsonAsString(true));
    }

    @Test
    public void LND_LookUpInvoice_ByRpcAPI() throws StatusException, SSLException, ValidationException {

        // only can look up from the invoice-creator
        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ERIN_GRPC_PORT,
                new File(ERIN_CERT),
                new File(ERIN_MACAROON));
        System.out.println("\nERIN\n");

        PaymentHash paymentHash = new PaymentHash();
        paymentHash.setRHashStr("d952f1e004369b4cd3f94c223fe2ca9cc1c6de8d23eb344003fa385e0fe32d42");

        System.out.println(synchronousLndAPI.lookupInvoice(paymentHash).toJsonAsString(true));
    }

    @Test
    public void LND_ListPayment_ByRpcAPI() throws StatusException, SSLException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));
        System.out.println("\nALICE\n");

        ListPaymentsRequest listPaymentsRequest = new ListPaymentsRequest();
        listPaymentsRequest.setIndexOffset(13);
        listPaymentsRequest.setReversed(false);
        listPaymentsRequest.setMaxPayments(5);

        System.out.println(synchronousLndAPI.listPayments(listPaymentsRequest).toJsonAsString(true));
    }

    @Test
    public void LND_ListInvoice_ByRpcAPI() throws StatusException, SSLException, ValidationException {
        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ERIN_GRPC_PORT,
                new File(ERIN_CERT),
                new File(ERIN_MACAROON));
        System.out.println("\nERIN\n");

        ListInvoiceRequest listInvoiceRequest = new ListInvoiceRequest();
        listInvoiceRequest.setIndexOffset(0);
        listInvoiceRequest.setNumMaxInvoices(5);

        System.out.println(synchronousLndAPI.listInvoices(listInvoiceRequest).toJsonAsString(true));
    }

    @Test
    public void LND_QueryRoute_ByRpcAPI() throws ClientSideException, SSLException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));
        System.out.println("\nAlice\n");

        QueryRoutesRequest req = new QueryRoutesRequest();
        // destination in decode payment request
        req.setPubKey("02ded0a280bdb225715901292bda186ef0d319c1cf3512b8626b7a72621c1aef46");
        // numSatoshis in decode payment request
        req.setAmt(50_000L);

        try {
            QueryRoutesResponse queryRoutesResponse = synchronousLndAPI.queryRoutes(req);
            System.out.println(queryRoutesResponse.toJsonAsString(true));
        } catch (ServerSideException e) {
            if ("UNKNOWN: unable to find a path to destination".equals(e.getMessage())) {
                System.out.println(e.getMessage());
            }
        } catch (StatusException | ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void LND_EstimateFee_ByRpcAPI() throws StatusException, SSLException, ValidationException {
        SynchronousLndAPI synchronousLndAPI_Alice = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));

        Map<String, Long> reqMap = new HashMap<>();
        reqMap.put(FRANK_PUB_KEY, 50_000L);

        EstimateFeeRequest request = new EstimateFeeRequest();
        request.setAddrToAmount(reqMap);

        EstimateFeeResponse estimateFeeResponse = synchronousLndAPI_Alice.estimateFee(request);
        System.out.println(estimateFeeResponse.toJsonAsString(true));
    }

    @Test
    public void LND_SendPayment_ByRpcAPI() throws StatusException, SSLException, ValidationException {
        // Alice
        SynchronousLndAPI synchronousLndAPI_Alice = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));
        System.out.println("\nAlice\n");

        FeeLimit feeLimit = new FeeLimit();
        feeLimit.setFixedMsat(2601);

        SendRequest sendRequest = new SendRequest();
        sendRequest.setPaymentRequest("lnbcrt500u1p3trq9ypp5m9f0rcqyx6d5e5lefs3rlck2nnqudh5dy04ngsqrlgu9urlr94pqdqqcqzpgsp5ysq44n6h5kdzmg2klph2gq0kvudr5lc5rudm4pds0gpvflrphlwq9qyyssqu5x9rg7lusm5es8y6n84qklcjdwssp2p0wg7mkn3sc67xwfsd76kgl59vhe64sgefcu69j8gzaegemscgmk323w7hrgqx59tn480pycpka9tfy");
//        sendRequest.setFeeLimit(feeLimit);
//        sendRequest.setAmt(40_000L);

        SendResponse sendResponse = synchronousLndAPI_Alice.sendPaymentSync(sendRequest);
        System.out.println(sendResponse.toJsonAsString(true));
    }

    @Test
    public void LND_CloseChannel_ByRpcAPI() throws StatusException, SSLException, ValidationException {

        SynchronousLndAPI synchronousLndAPI_Alice = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));

        ChannelPoint channelPoint = new ChannelPoint();
        // before :
        channelPoint.setFundingTxidStr("0c50ff69a260ba757c72bfbefea7ab13c5bce52afbc14334a004503f82d83a0f");
        // after :
        channelPoint.setOutputIndex(1);

        CloseChannelRequest closeChannelRequest = new CloseChannelRequest();
        // channel point
        closeChannelRequest.setChannelPoint(channelPoint);
        // force to close the channel
        closeChannelRequest.setForce(false);
        // target number of blocks that the closure transaction should be confirmed by.
        closeChannelRequest.setTargetConf(3);

        // request
        Iterator<CloseStatusUpdate> result = synchronousLndAPI_Alice.closeChannel(closeChannelRequest);
        while (result.hasNext()) {
            System.out.println("Received Update: " + result.next().toJsonAsString(true));
        }

        // close stub
        synchronousLndAPI_Alice.close();
    }

    @Deprecated
    public void LND_SubscribeInvoices_ByRpcAPI() throws StatusException, SSLException, ValidationException, InterruptedException {
        AsynchronousLndAPI asynchronousLndAPI = new AsynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));


        InvoiceSubscription invoiceSubscription = new InvoiceSubscription();
        invoiceSubscription.setAddIndex(10);
        invoiceSubscription.setSettleIndex(10);

        asynchronousLndAPI.subscribeInvoices(0L, 0L, new StreamObserver<Invoice>() {
            @Override
            public void onNext(Invoice value) {
                System.out.println("Next:");
                System.out.println(value.toJsonAsString(true));
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error:");
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed");
            }
        });

        Thread.currentThread().join();
        System.out.println("Stop");
    }


    // ************************************************** Rest Request **************************************************


    @Test
    public void LND_SyncRestTest() throws IOException, NoSuchAlgorithmException, KeyManagementException, CertificateException, InterruptedException {

        String who_cert = ALICE_CERT;
        String who_macaroon = ALICE_MACAROON;
        String who_host = ALICE_REST_HOST;
//        String which_path = "/v1/getinfo";
        String which_path = "/v1/channels/stream";

        // building url
        HttpUrl.Builder urlBuilder = HttpUrl.parse(who_host + which_path).newBuilder();
        String url = urlBuilder
                .build()
                .toString();

        JSONObject reqJson = new JSONObject();
        reqJson.put("nodePubkey", java.util.Base64.getEncoder().encodeToString(Numeric.hexStringToByteArray(ERIN_PUB_KEY)));
        reqJson.put("localFundingAmount", 100_000L);
        reqJson.put("pushSat", 5_000L);
        System.out.println(">>> request:" + reqJson);

        // building request
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson.toString(), MediaType.parse("application/json")))
                .header("Grpc-Metadata-macaroon", Hex.encodeHexString(Files.readAllBytes(Paths.get(who_macaroon))))
                .build();

        // this is for verifying
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final Certificate certificate = cf.generateCertificate(new FileInputStream(who_cert));

        // do request
        String s = LND_FormatRestReq_Async(request, certificate);
        System.out.println(s);

        Thread.currentThread().join();
    }

    /**
     * 真正请求, 异步方式
     */
    private String LND_FormatRestReq_Async(Request request, Certificate certificate)
            throws NoSuchAlgorithmException, KeyManagementException {

        // For TrustManager
        X509TrustManager TRUST_FILES_CERTS = generateTrustManagerByFile(certificate);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{TRUST_FILES_CERTS}, new SecureRandom());

        OkHttpClient okHttpClient_sin = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), TRUST_FILES_CERTS)
                .readTimeout(10 * 5, TimeUnit.MINUTES)
                .build();

        okHttpClient_sin.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("<<< Failure with exception:" + e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ObjectMapper om = new ObjectMapper();
                String str = om.writerWithDefaultPrettyPrinter().writeValueAsString(JSON.parse(response.body().string()));
                System.out.println("<<< Successful response:" + str);
            }
        });

        return ">>>> Request sending";
    }


    /**
     * 真正请求, 同步方式
     */
    private ResponseBody LND_FormatRestReq_Sync(Request request, Certificate certificate)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {

        //        // encode binary macaroon into hex string
//        String macaroonHexStr = Hex.encodeHexString(Files.readAllBytes(Paths.get(who_macaroon)));
//        // When using Numeric, it will automatically add 0x, at the front... but LND would not accept this 0x
//        String macaroon0xStr = Numeric.toHexString(Files.readAllBytes(Paths.get(who_macaroon)));
//        if (macaroon0xStr.substring(2).equals(macaroonHexStr)) {
//            System.out.println(">>> They are the same");
//        }


        // For TrustManager
        X509TrustManager TRUST_FILES_CERTS = generateTrustManagerByFile(certificate);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{TRUST_FILES_CERTS}, new SecureRandom());

        OkHttpClient okHttpClient_sin = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), TRUST_FILES_CERTS)
                .build();
        Call call = okHttpClient_sin.newCall(request);

        return call.execute().body();
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


    /**
     * c-lightning test
     */
    @Test
    public void CLIGHTNING_SyncChannelBalanceTest() throws IOException, NoSuchAlgorithmException, KeyManagementException {

        // toBase64String already contains calling the encode function
        byte[] macaroonBytes = Files.readAllBytes(Paths.get(BOB_MACAROON));
        String macaroonBase64 = Base64.toBase64String(macaroonBytes);

        String real_base64 = "AgELYy1saWdodG5pbmcCPk1vbiBNYXkgMjMgMjAyMiAwMzo1MzoyMSBHTVQrMDAwMCAoQ29vcmRpbmF0ZWQgVW5pdmVyc2FsIFRpbWUpAAAGIFt67R66vxKgyh+4mRWNozBf+La8r4cxrTQWgeH6Laxp";
        if (macaroonBase64.equals(real_base64)) {
            System.out.println("Base64 equals");
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BOB_REST_API + "v1/getinfo").newBuilder();
        String url = urlBuilder
                .build()
                .toString();

        Request requestGet = new Request.Builder()
                .url(url)
                .header("macaroon", macaroonBase64)
                .build();

        Call call = okHttpClient.newCall(requestGet);

        ResponseBody body = call.execute().body();
        if (null != body) {
            System.out.println();
            System.out.println(body.string());
            System.out.println();
        }
    }



    // ************************************************** Raw Lightning Grpc Request  **************************************************
    @Test
    public void LND_RAW_Test() {

    }

}
