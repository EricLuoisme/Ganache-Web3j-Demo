package com.example.lightning;

import com.example.lighting.MacaroonCallCredential;
import com.example.lightning.LightningGrpc.LightningBlockingStub;
import com.example.lightning.router.*;
import com.example.lightning.router.RouterGrpc.RouterBlockingStub;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.web3j.utils.Numeric;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Using Raw Lightning-Grpc interact with Polar
 *
 * @author Roylic
 * 2022/6/24
 */
public class PolarLocalRawGrpcTest {

    private final static String POLAR_BASE_URL = "https://127.0.0.1:";
    private final static String POLAR_MACAROON_LOC = "/Users/pundix2022/.polar/networks/1/volumes/lnd";
    private final static String POLAR_FILE_LOC_WIN = "C:\\Users\\lykis\\.polar\\networks\\1\\volumes\\lnd";

    // Alice
    private final static int ALICE_GRPC_PORT = 10001;
    private final static String ALICE_REST_HOST = "https://127.0.0.1:8081";
    private final static String ALICE_CERT = "/alice/tls.cert";
    private final static String ALICE_MACAROON = "/alice/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ALICE_PUB_KEY = "0226feae41cc2eb18455df8707c6e005ec3ac2c8e64c3039b1409800995daea2be";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_REST_HOST = "https://127.0.0.1:8084";
    private final static String DAVE_CERT = "/dave/tls.cert";
    private final static String DAVE_MACAROON = "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "036d42b106e8813f9bca46c507c3d540ef944bc99b94ab3e1a388eee61a9d90948";

    // Erin
    private final static int ERIN_GRPC_PORT = 10005;
    private final static String ERIN_REST_HOST = "https://127.0.0.1:8085";
    private final static String ERIN_CERT = "/erin/tls.cert";
    private final static String ERIN_MACAROON = "/erin/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ERIN_PUB_KEY = "03d6714b621817ebe8bfcfffe302a3d0d885129e9b3a3a140cf17b6114dd480aad";

//    // for bench mark testing
//    public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);
//    }

    @Test
    public void connectionTest() throws IOException {
        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC_WIN, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);
        WalletBalanceResponse walletBalanceResponse = lightningBlockingStub.walletBalance(WalletBalanceRequest.newBuilder().build());
        System.out.println(walletBalanceResponse.toString());
    }


    /**
     * Decode for getting:
     * - num_msat -> check mis-behavior
     * - payment hash -> tack payment use
     * - destination + timestamp + expiry -> send payment use
     */
    @Test
    public void decodePaymentReqTest() throws IOException {
        // stub
        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC_WIN, DAVE_CERT, DAVE_GRPC_PORT, DAVE_MACAROON);

        String payReqStr = "lnbcrt10u1p3t0afvpp54mx8cpm97ykuc3k89n2g8f0m6gzrp4znuu9uuttv0zks9zvu2ptqdqqcqzpgsp5efr2s9mek3ug2nrd2wlu53jw0wargcfg8p3vd955ux3pnl9k4xgs9qyyssqg8ra39gaugux5zskmmjp3aa0lhneh0q208xzmqtdrr0ehgq9vlj8n583v6preg8qqus3drwkqfyvp0nrqzj3gyegx45nk3cwcs0ylqqqalw8t4";

        // req
        PayReqString req = PayReqString.newBuilder()
                .setPayReq(payReqStr)
                .build();
        PayReq payReq = lightningBlockingStub.decodePayReq(req);
        System.out.println(payReq.toString());
        System.out.println(">>> Payment Hash: " + payReq.getPaymentHash());
//        System.out.println(">>> Hex Payment Addr: " + Hex.encodeHexString(payReq.getPaymentAddr().toByteArray()));
    }

    /**
     * Estimate the route fee for getting:
     * - routingFeeMSat -> send payment use
     */
    @Test
    public void estimateRouteFee() throws IOException {
        // stub
        RouterBlockingStub routerBlockingStub = getRouterBlockingStub(
                POLAR_FILE_LOC_WIN, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);
        RouteFeeRequest req = RouteFeeRequest.newBuilder()
                .setAmtSat(10000)
                .setDest(ByteString.copyFrom(Numeric.hexStringToByteArray(ERIN_PUB_KEY)))
                .build();
        RouteFeeResponse routeFeeResponse = routerBlockingStub.estimateRouteFee(req);
        System.out.println(">>> Routing Fee: " + routeFeeResponse.getRoutingFeeMsat() + " mSat");
        System.out.println(">>> Time Lock Delay: " + routeFeeResponse.getTimeLockDelay());
    }

    @Test
    public void routeQuery() throws IOException {
        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC_WIN, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);
        QueryRoutesRequest routeReq = QueryRoutesRequest.newBuilder()
                .setAmt(10000)
                .setFeeLimit(
                        FeeLimit.newBuilder()
//                                .setFixedMsat(1000)
                                .build())
                .setPubKey(ERIN_PUB_KEY)
                .build();
        QueryRoutesResponse queryRoutesResponse = lightningBlockingStub.queryRoutes(routeReq);
        System.out.println(">>> " + queryRoutesResponse);
        System.out.println(">>> Total Fees in MSat: " + queryRoutesResponse.getRoutes(0).getTotalFeesMsat());
        System.out.println(">>> Hops: " + queryRoutesResponse.getRoutesList().get(0).getHopsCount());
    }

    /**
     * Send payment synchronised, if success, no creation timestamp would return
     */
    @Test
    public void sendPaymentTest() throws IOException {
        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC_WIN, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);

        String payReqStr = "lnbcrt10u1p3t0afvpp54mx8cpm97ykuc3k89n2g8f0m6gzrp4znuu9uuttv0zks9zvu2ptqdqqcqzpgsp5efr2s9mek3ug2nrd2wlu53jw0wargcfg8p3vd955ux3pnl9k4xgs9qyyssqg8ra39gaugux5zskmmjp3aa0lhneh0q208xzmqtdrr0ehgq9vlj8n583v6preg8qqus3drwkqfyvp0nrqzj3gyegx45nk3cwcs0ylqqqalw8t4";
        SendRequest req = SendRequest.newBuilder()
                .setPaymentRequest(payReqStr)
                .setFeeLimit(
                        FeeLimit.newBuilder()
                                .setFixedMsat(3030)
                                .build())
                .build();

        SendResponse sendResponse = lightningBlockingStub.sendPaymentSync(req);
        System.out.println(sendResponse.toString());
        System.out.println(">>> Real Total Amt in MSat: " + sendResponse.getPaymentRoute().getTotalAmtMsat());
        System.out.println(">>> Real Total Fees in MSat: " + sendResponse.getPaymentRoute().getTotalFeesMsat());
        System.out.println(">>> Real Hops: " + sendResponse.getPaymentRoute().getHopsCount());
    }


    /**
     * Judge an payment is been paid, using the PaymentHash from decode-req-result
     * Only the one who paid the invoice could got an outcome, otherwise, exception
     */
    @Test
    public void trackPaymentTest() throws IOException {
//        RouterBlockingStub routerBlockingStub = getRouterBlockingStub(
//                POLAR_FILE_LOC_WIN, ERIN_CERT, ERIN_GRPC_PORT, ERIN_MACAROON);

        RouterBlockingStub routerBlockingStub = getRouterBlockingStub(
                POLAR_FILE_LOC_WIN, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);

        // req
        TrackPaymentRequest req = TrackPaymentRequest.newBuilder()
                .setPaymentHash(
                        ByteString.copyFrom(
                                Numeric.hexStringToByteArray("aecc7c0765f12dcc46c72cd483a5fbd20430d453e70bce2d6c78ad02899c5056")))
                .build();

        try {
            Iterator<Payment> paymentIterator = routerBlockingStub.trackPaymentV2(req);
            while (paymentIterator.hasNext()) {
                System.out.println(">>> Got an update");
                Payment payment = paymentIterator.next();
                System.out.println(">>> Real Hops: " + payment.getHtlcsList().get(0).getRoute().getHopsCount());
                System.out.println(">>> Real Fee in MSat: " + payment.getFeeMsat());
                System.out.println(">>> Status: " + payment.getStatus().name());
                System.out.println(">>> Creation Data: " + payment.getCreationTimeNs());
            }
        } catch (StatusRuntimeException e) {
            // catch grpc exceptions
            if (e.getMessage().contains("NOT_FOUND")) {
                System.out.println(">>> Easy, " + e.getMessage());
            } else {
                e.printStackTrace();
            }
        }


    }


    /**
     * Would get notification from:
     * - created an invoice & received an payment of invoice
     */
    @Test
    public void subscribeInvoice() throws IOException {
        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC_WIN, ERIN_CERT, ERIN_GRPC_PORT, ERIN_MACAROON);

        Iterator<Invoice> invoiceIterator = lightningBlockingStub.subscribeInvoices(InvoiceSubscription.newBuilder().build());
        while (invoiceIterator.hasNext()) {
            Invoice next = invoiceIterator.next();
            System.out.println("This Invoice Status: " + next.getState().name());
            if (next.getState().getNumber() == 1) {
                System.out.println("Detail for this Settled Invoice: " + next.toString());
            }
        }
    }

    private static LightningBlockingStub getLightningBlockingStub(String filePath, String certPath, Integer grpcPort, String macaroonPath) throws IOException {
        ManagedChannel channel = getChannel("127.0.0.1", filePath + certPath, grpcPort);
        String macaroon = Hex.encodeHexString(Files.readAllBytes(Paths.get(filePath + macaroonPath)));
        // stub
        return LightningGrpc
                .newBlockingStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));
    }

    private static RouterBlockingStub getRouterBlockingStub(String filePath, String certPath, Integer grpcPort, String macaroonPath) throws IOException {
        ManagedChannel channel = getChannel("127.0.0.1", filePath + certPath, grpcPort);
        String macaroon = Hex.encodeHexString(Files.readAllBytes(Paths.get(filePath + macaroonPath)));
        // stub
        return RouterGrpc
                .newBlockingStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));
    }

    private static ManagedChannel getChannel(String hostIp, String certPath, Integer grpcPort) throws SSLException {
        SslContext sslContext = GrpcSslContexts.forClient().trustManager(new File(certPath)).build();
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(hostIp, grpcPort);
        return channelBuilder.sslContext(sslContext).build();
    }

}
