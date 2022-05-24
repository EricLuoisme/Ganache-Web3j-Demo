package com.example.ganacheweb3jdemo;


import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;

import java.io.File;
import java.io.IOException;

/**
 * 尝试进行RPC解析测试
 *
 * @author Roylic
 * @date 2022/4/21
 */
public class LocalLightningNodeCallingTest {

    private final static int LOCAL_LND_GRPC_PORT = 10009;
    private final static String LOCAL_LND_BASE_LOC = "/Users/pundix2022/Library/Application Support/Lnd";
    private final static String LOCAL_LND_CERT_LOC = LOCAL_LND_BASE_LOC + "/tls.cert";
    private final static String LOCAL_LND_MACAROON_LOC = LOCAL_LND_BASE_LOC + "/data/chain/bitcoin/testnet/admin.macaroon";

    @Test
    public void syncChannelBalanceTest() throws IOException, StatusException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                LOCAL_LND_GRPC_PORT,
                new File(LOCAL_LND_CERT_LOC),
                new File(LOCAL_LND_MACAROON_LOC));

        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));

    }


}
