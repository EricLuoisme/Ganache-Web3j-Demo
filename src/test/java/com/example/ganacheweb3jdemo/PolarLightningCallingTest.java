package com.example.ganacheweb3jdemo;

import com.example.ganacheweb3jdemo.web3j.ApplicationInterceptorImp;
import com.example.ganacheweb3jdemo.web3j.LogInterceptorImp;
import okhttp3.*;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.*;
import org.web3j.utils.Numeric;

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
    private final static String ALICE_PUB_KEY = "0367e5f2c0cc443c89354def534335041bae6f32b62963df9755869dc2f0e47d26";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_CERT = POLAR_MACAROON_LOC + "/dave/tls.cert";
    private final static String DAVE_MACAROON = POLAR_MACAROON_LOC + "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "03dede445f0744c0914b52ecb3890b71059a51b1f418ecc8897667445ab77f3967";

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
    public void LND_SyncChannelBalanceTest() throws IOException, StatusException, ValidationException {

        SynchronousLndAPI synchronousLndAPI_Alice = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));

        System.out.println(synchronousLndAPI_Alice.listChannels(new ListChannelsRequest()).toJsonAsString(true));
        System.out.println(synchronousLndAPI_Alice.channelBalance().toJsonAsString(true));
        System.out.println(synchronousLndAPI_Alice.walletBalance().toJsonAsString(true));
//        System.out.println(synchronousLndAPI_Alice.listPeers(false).toJsonAsString(true));
    }

    @Test
    public void LND_OpenChannel_ByRestAPI() throws StatusException, IOException, ValidationException {

        SynchronousLndAPI synchronousLndAPI_Alice = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));

        OpenChannelRequest openChannelRequest = new OpenChannelRequest();
        openChannelRequest.setNodePubkey(Numeric.hexStringToByteArray(DAVE_PUB_KEY));
        // The number of SATs the wallet should commit to the channel
        openChannelRequest.setLocalFundingAmount(100_000L);
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
        Iterator<OpenStatusUpdate> result = synchronousLndAPI_Alice.openChannel(openChannelRequest);
        while (result.hasNext()) {
            System.out.println("Received Update: " + result.next().toJsonAsString(true));
        }

        // close stub
        synchronousLndAPI_Alice.close();
    }





    // ************************************************** c-lightning Nodes **************************************************
    @Test
    public void CLIGHTNING_SyncChannelBalanceTest() throws IOException, ClientSideException {

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
