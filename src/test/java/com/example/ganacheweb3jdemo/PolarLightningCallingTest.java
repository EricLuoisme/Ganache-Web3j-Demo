package com.example.ganacheweb3jdemo;

import com.example.ganacheweb3jdemo.web3j.ApplicationInterceptorImp;
import com.example.ganacheweb3jdemo.web3j.LogInterceptorImp;
import com.github.nitram509.jmacaroons.Macaroon;
import com.github.nitram509.jmacaroons.MacaroonsBuilder;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_CERT = POLAR_MACAROON_LOC + "/dave/tls.cert";
    private final static String DAVE_MACAROON = POLAR_MACAROON_LOC + "/dave/data/chain/bitcoin/regtest/admin.macaroon";

    // Bob - c-lightning
    private final static String BOB_REST_API = "http://127.0.0.1:8182";
    private final static String BOB_MACAROON = "/Users/pundix2022/.polar/networks/1/volumes/c-lightning/bob/rest-api/access.macaroon";
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(new ApplicationInterceptorImp())
            .addNetworkInterceptor(new LogInterceptorImp())
            .retryOnConnectionFailure(false)
            .build();


    @Test
    public void LND_syncChannelBalanceTest() throws IOException, StatusException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                ALICE_GRPC_PORT,
                new File(ALICE_CERT),
                new File(ALICE_MACAROON));

        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));
    }

    @Test
    public void CLIGHTNING_syncChannelBalanceTest() throws ClientSideException, FileNotFoundException {

        File file = new File(BOB_MACAROON);
        StaticFileMacaroonContext context = new StaticFileMacaroonContext(file);
        String currentMacaroonAsHex = context.getCurrentMacaroonAsHex();


//        Request requestGet = new Request.Builder()
//                .url(BOB_REST_API + "/v1/getinfo")
//                .header("macaroon", currentMacaroonAsHex)
//                .build();
//
//        Call call = okHttpClient.newCall(requestGet);
//        RequestBody body = call.request().body();
//        System.out.println();

    }


}
