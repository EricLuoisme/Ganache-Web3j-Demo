package com.example.lightning;

import com.example.lighting.MacaroonCallCredential;
import com.example.lightning.LightningGrpc.LightningBlockingStub;
import com.example.lightning.router.RouterGrpc;
import com.example.lightning.router.RouterGrpc.RouterBlockingStub;
import com.example.lightning.router.TrackPaymentRequest;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.web3j.utils.Numeric;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

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
    private final static String ALICE_PUB_KEY = "02c94f36b8574122ecf189a90fea84f02b8b80c21577f60499a2345ae9621c9fb4";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_REST_HOST = "https://127.0.0.1:8084";
    private final static String DAVE_CERT = "/dave/tls.cert";
    private final static String DAVE_MACAROON = "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "03b042a9d98111696f7aa00815dd8c740a55afb452bf89de33a3001ce8a03f8f65";

    // Erin
    private final static int ERIN_GRPC_PORT = 10005;
    private final static String ERIN_REST_HOST = "https://127.0.0.1:8085";
    private final static String ERIN_CERT = "/erin/tls.cert";
    private final static String ERIN_MACAROON = "/erin/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ERIN_PUB_KEY = "034236bdc8b2b4d5ad0c3292364d514f5fc12008172531d6fe48853553a9948a7b";


    @Test
    public void connectionTest() throws IOException {
        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC_WIN, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);
        WalletBalanceResponse walletBalanceResponse = lightningBlockingStub.walletBalance(WalletBalanceRequest.newBuilder().build());
        System.out.println(walletBalanceResponse.toString());
    }


    @Test
    public void decodePaymentReqTest() throws IOException {
        // stub
        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC_WIN, DAVE_CERT, DAVE_GRPC_PORT, DAVE_MACAROON);
        // req
        PayReqString req = PayReqString.newBuilder()
                .setPayReq("lnbcrt500u1p3ttpt4pp5l79yvv3narfv6gla9gvgfdttvf03j6s0fmqxvrnlpm6k3x76ne6sdqqcqzpgsp5y3ekzn4l9gwdupn26lusezr4eun5rkuxrluzv3ucumuq9y29xjzq9qyyssq00ytzu49tp7m3acgrq6kfx29gwncrxzmazcu0seczyg9jkvums8hdkx082klj39rej0z4mamvddzttl7vlhrf9w95tu6w26rq5sxe7cp3ukpxq")
                .build();
        PayReq payReq = lightningBlockingStub.decodePayReq(req);
        System.out.println(payReq.toString());
        System.out.println(">>> Hex Payment Addr" + Hex.encodeHexString(payReq.getPaymentAddr().toByteArray()));
    }


    @Test
    public void trackPaymentTest() throws IOException {
        RouterBlockingStub routerBlockingStub = getRouterBlockingStub(
                POLAR_FILE_LOC_WIN, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);

        // req
        TrackPaymentRequest req = TrackPaymentRequest.newBuilder()
                .setPaymentHash(
                        ByteString.copyFrom(
                                Numeric.hexStringToByteArray("42dfa58e4137db25ccbd6397bd16e79791ba53bccd6ba2543c14eeee1763b049")))
                .build();

        Iterator<Payment> paymentIterator = routerBlockingStub.trackPaymentV2(req);
        while (paymentIterator.hasNext()) {
            System.out.println(paymentIterator.next().toString());
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

    // form ssl channel
    private static ManagedChannel getChannel(String hostIp, String certPath, Integer grpcPort) throws SSLException {
        SslContext sslContext = GrpcSslContexts.forClient().trustManager(new File(certPath)).build();
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(hostIp, grpcPort);
        return channelBuilder.sslContext(sslContext).build();
    }

}
