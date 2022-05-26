package com.example.ganacheweb3jdemo;

import com.example.ganacheweb3jdemo.web3j.ApplicationInterceptorImp;
import com.example.ganacheweb3jdemo.web3j.LogInterceptorImp;
import okhttp3.*;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.*;
import org.web3j.utils.Numeric;

import javax.net.ssl.SSLException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


/**
 * 尝试进行RPC解析测试
 *
 * @author Roylic
 * @date 2022/4/21
 */
public class PolarLightningCallingTest {

    private final static String POLAR_BASE_URL = "https://127.0.0.1:";
    private final static String POLAR_MACAROON_LOC = "/Users/pundix2022/.polar/networks/1/volumes/lnd";

    // Alice
    private final static int ALICE_GRPC_PORT = 10001;
    private final static String ALICE_CERT = POLAR_MACAROON_LOC + "/alice/tls.cert";
    private final static String ALICE_MACAROON = POLAR_MACAROON_LOC + "/alice/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ALICE_PUB_KEY = "02667dc9cba7b7ecde8d93a17fa74159f4ac3822892fed0a47c1344d7a2ff9d379";

    // Erin
    private final static int ERIN_GRPC_PORT = 10005;
    private final static String ERIN_CERT = POLAR_MACAROON_LOC + "/erin/tls.cert";
    private final static String ERIN_MACAROON = POLAR_MACAROON_LOC + "/erin/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ERIN_PUB_KEY = "0363151ad22fc77b6de81ded9e416d3a5db08c5c2259ac902ee5be8d50fb6beb4a";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_CERT = POLAR_MACAROON_LOC + "/dave/tls.cert";
    private final static String DAVE_MACAROON = POLAR_MACAROON_LOC + "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "0398690d55825b8fa6b7ac2b05f68e390b65e0b80e8a9e9edc2631884a43432f49";

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


    // ************************************************** LND Nodes *********************************************************
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
//        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.walletBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.listInvoices(new ListInvoiceRequest()).toJsonAsString(true));
//        System.out.println(synchronousLndAPI.getChanInfo(148434069815297L));

        SendRequest sendRequest = new SendRequest();
        sendRequest.setPaymentRequest("lnbcrt500u1p3g73jspp5v8s6mywu3mlcz38y8axej6fus3y4ex8v3qfhfqxzw6md45ngznnsdqqcqzpgsp5ffxyh20dc4ztv6lwaj9k6pf7n9n5w705xzuuu6pu9mpangtnxfjs9qyyssq255dnth59h7dgu9nyh2ytu2j2r57npqy4w6esyv8tk5w3skj90uhgy9vrsqxy33sxq85aqqh59jq685anzxlm0w9fsk4y72jvls3eggq622hzh");

        SendResponse sendResponse = synchronousLndAPI.sendPaymentSync(sendRequest);
        System.out.println(sendResponse.toJsonAsString(true));

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
//        System.out.println(synchronousLndAPI.listChannels(new ListChannelsRequest()).toJsonAsString(true));
//        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.walletBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI.getChanInfo(130841883770880L));
        System.out.println(synchronousLndAPI.listInvoices(new ListInvoiceRequest()));
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
//        System.out.println(synchronousLndAPI.getChanInfo(130841883770880L));

        ListInvoiceRequest listInvoiceRequest = new ListInvoiceRequest();
        listInvoiceRequest.setPendingOnly(true);
        System.out.println(synchronousLndAPI.listInvoices(listInvoiceRequest));
    }


    @Test
    public void LND_OpenChannel_ByRpcAPI() throws StatusException, IOException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ERIN_GRPC_PORT,
                new File(ERIN_CERT),
                new File(ERIN_MACAROON));

        OpenChannelRequest openChannelRequest = new OpenChannelRequest();
        openChannelRequest.setNodePubkey(Numeric.hexStringToByteArray(DAVE_PUB_KEY));
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
        invoice.setMemo("Dave's invoice should be paid by Alice 10,000");
        // The value of this invoice in satoshis The fields value and value_msat are mutually exclusive.
        // if the value is smaller than node's routing policy, this invoice could not 'see' this route
        invoice.setValue(10_000L);

        AddInvoiceResponse addInvoiceResponse = synchronousLndAPI.addInvoice(invoice);
        System.out.println(addInvoiceResponse.toJsonAsString(true));
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

        SendRequest sendRequest = new SendRequest();
        sendRequest.setPaymentRequest("lnbcrt100u1p3g73a7pp5fcaxhz6zncrw342xqpuehxv3g0767qj0ejy4jpg3u4dh8udey02qdzgg3shvef8wvsxjmnkda5kxefqwd5x7atvvssxyefqwpskjepqvfujqstvd93k2gp3xqkrqvpscqzpgsp5lrlue7ch6lecspy0sp2y76aleylx9tplv9guc884k7097x9ew82q9qyyssqly5r9yd80xff9mg8kvw3au2d6eyzjm3t2fex68pcht8zr0hxm3n42x8kyp6kek82j03gr6pw995wsr2rlw42sav7rl9ae4u7apw47ncpmdk0k5");

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
        channelPoint.setFundingTxidStr("0386fc8cc01a503b7b304034cdac9003005472efe78b286ec1b9e720ee3050fb");
        // after :
        channelPoint.setOutputIndex(0);

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


    // ************************************************** c-lightning Nodes **************************************************
    @Test
    public void CLIGHTNING_SyncChannelBalanceTest() throws IOException {

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
}
