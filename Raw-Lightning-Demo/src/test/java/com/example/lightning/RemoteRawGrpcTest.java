package com.example.lightning;

import com.example.lighting.MacaroonCallCredential;
import com.example.lightning.router.RouterGrpc;
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
 * @author Roylic
 * 2022/6/24
 */
public class RemoteRawGrpcTest {


    private final static String NODE_IP = "18.139.32.218";
    private final static int NODE_GRPC_PORT = 10009;
    private final static int NODE_REST_PORT = 8080;
    private final static String NODE_PUB = "0224c04dd5b928c15d52bae00aeba47511ee4ec8fe594465d9b0be2f413c4a9a69";

    private final static String FILE_BASE_PATH = "/Users/pundix2022/Desktop/开发项目/LightningNetworkRelated/";
    private final static String CERT_PATH = FILE_BASE_PATH + "tls.cert";
    private final static String MACAROON_PATH = FILE_BASE_PATH + "admin.macaroon";


    private final static String NODE_IP_2 = "18.140.86.31";
    private final static int NODE_GRPC_PORT_2 = 10009;
    private final static int NODE_REST_PORT_2 = 8080;
    private final static String NODE_PUB_2 = "033464d76c9bea1c3a40f410ca3adeffe7cce14085fc40da6a1779cb75169687d4";

    private final static String CERT_PATH_2 = FILE_BASE_PATH + "tls-2.cert";
    private final static String MACAROON_PATH_2 = FILE_BASE_PATH + "admin-2.macaroon";

    @Test
    public void decodePaymentReqTest() throws IOException {
        ManagedChannel channel = getChannel(NODE_IP, CERT_PATH, NODE_GRPC_PORT);
        String macaroon =
                Hex.encodeHexString(
                        Files.readAllBytes(Paths.get(MACAROON_PATH))
                );

        // stub
        LightningGrpc.LightningBlockingStub lightningBlockingStub = LightningGrpc
                .newBlockingStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));

        // req
        PayReqString req = PayReqString.newBuilder()
                .setPayReq("lntb1u1p3t2h82pp5qnc9f9vk6acs9l35zc9chly7ghr4nevpnt6xragqklf46nkdc4dsdq2g35kumn9wgcqzpgxqrrsssp5929clsx890uk3gg034uw4328mnhhdaesy2qjjnfjcm5d5h3hr2as9qyyssqvgcewu2agejqy5sepvc2gttyw4tlwhyk7fa52ntknskfjxcnpr6x8avmm4gs22wxv9qvym8kvqsm82zqkqjme0khrf28utqhn94mqrsptue0ep")
                .build();

        PayReq payReq = lightningBlockingStub.decodePayReq(req);
        System.out.println(payReq.toString());
        System.out.println(">>> Payment Hash: " + payReq.getPaymentHash());
        System.out.println(">>> Hex Payment Addr: " + Hex.encodeHexString(payReq.getPaymentAddr().toByteArray()));
    }


    @Test
    public void trackPaymentTest() throws IOException {
        ManagedChannel channel = getChannel(NODE_IP, CERT_PATH, NODE_GRPC_PORT);
        String macaroon =
                Hex.encodeHexString(
                        Files.readAllBytes(Paths.get(MACAROON_PATH))
                );

        // stub
        RouterGrpc.RouterBlockingStub routerBlockingStub = RouterGrpc.
                newBlockingStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));

        // req
        TrackPaymentRequest req = TrackPaymentRequest.newBuilder()
                .setPaymentHash(
                        ByteString.copyFrom(
                                Numeric.hexStringToByteArray("04f0549596d77102fe34160b8bfc9e45c759e5819af461f500b7d35d4ecdc55b")))
                .build();

        Iterator<Payment> paymentIterator = routerBlockingStub.trackPaymentV2(req);
        while (paymentIterator.hasNext()) {
            System.out.println(paymentIterator.next().toString());
        }
    }

    // form ssl channel
    private static ManagedChannel getChannel(String hostIp, String certPath, Integer grpcPort) throws SSLException {
        SslContext sslContext = GrpcSslContexts.forClient().trustManager(new File(certPath)).build();
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(hostIp, grpcPort);
        return channelBuilder.sslContext(sslContext).build();
    }

}
