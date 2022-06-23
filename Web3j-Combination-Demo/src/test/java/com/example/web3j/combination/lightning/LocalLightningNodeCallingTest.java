package com.example.web3j.combination.lightning;


import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.SendRequest;
import org.lightningj.lnd.wrapper.message.SendResponse;

import javax.net.ssl.SSLException;
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

    @Test
    public void syncPaymentTest() throws StatusException, SSLException, ValidationException {

        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(
                "127.0.0.1",
                LOCAL_LND_GRPC_PORT,
                new File(LOCAL_LND_CERT_LOC),
                new File(LOCAL_LND_MACAROON_LOC));

        SendRequest sendRequest = new SendRequest();
        sendRequest.setPaymentRequest("lntb100u1p32r5vlpp5kjukcts5dgczpw0ld933n2nfjvmr3zf73ycg0fygu95syp94we4sdq9veuxkcqzpgxqyz5vqsp5umc6zxwzv5t87070zhpp78yeuvr9g0cva0p805u2nlv6xew28ehq9qyyssq78cqp00mevhlf8wdddnake7edz4dys4tg8he7g058v8a9fm37vwry0z27td8yskcanwc3l3f5kdc75wz4jlsp4sn5qgep345y42p54cp7d68za");

        SendResponse sendResponse = synchronousLndAPI.sendPaymentSync(sendRequest);
        System.out.println(sendResponse.toJsonAsString(true));
    }





}
