package com.example.lightning;

import com.example.lighting.MacaroonCallCredential;
import com.example.lightning.LightningGrpc.LightningBlockingStub;
import com.example.lightning.router.RouterGrpc;
import com.example.lightning.router.RouterGrpc.RouterBlockingStub;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Using Raw Lightning-Grpc interact with Polar
 *
 * @author Roylic
 * 2022/6/24
 */
public class PolarChannelRawGrpcTest {

    private final static String POLAR_BASE_URL = "https://127.0.0.1:";
    private final static String POLAR_FILE_LOC_MAC = "/Users/pundix2022/.polar/networks/1/volumes/lnd";
    //    private final static String POLAR_FILE_LOC_MAC = "/Users/roylic/.polar/networks/1/volumes/lnd";
    private final static String POLAR_FILE_LOC_WIN = "C:\\Users\\lykis\\.polar\\networks\\1\\volumes\\lnd";
    private final static String POLAR_FILE_LOC = POLAR_FILE_LOC_MAC;

    // Alice
    private final static int ALICE_GRPC_PORT = 10001;
    private final static String ALICE_REST_HOST = "https://127.0.0.1:8081";
    private final static String ALICE_CERT = "/alice/tls.cert";
    private final static String ALICE_MACAROON = "/alice/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ALICE_PUB_KEY = "02d2e44b74742b38611971dc859ac2dd4a7566caf57f45fafab6c27b921ff8c64e";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_REST_HOST = "https://127.0.0.1:8084";
    private final static String DAVE_CERT = "/dave/tls.cert";
    private final static String DAVE_MACAROON = "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "02a54fe7062e8c2ecc16829bd441418de1fbd58ba0dced42c966c37a7cb7bfceb0";

    // Erin
    private final static int ERIN_GRPC_PORT = 10005;
    private final static String ERIN_REST_HOST = "https://127.0.0.1:8085";
    private final static String ERIN_CERT = "/erin/tls.cert";
    private final static String ERIN_MACAROON = "/erin/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ERIN_PUB_KEY = "037129aab2d88801a28c2acf53d11cea9454fdaa7809ed02b1b303ce9487ca2dac";


    @Test
    public void getChannelInfo() throws IOException {

        LightningBlockingStub lightningBlockingStub = getLightningBlockingStub(
                POLAR_FILE_LOC, ALICE_CERT, ALICE_GRPC_PORT, ALICE_MACAROON);

        ListChannelsRequest channelsRequest = ListChannelsRequest.newBuilder().build();
        ListChannelsResponse listChannels = lightningBlockingStub.listChannels(channelsRequest);
        List<Channel> channelsList = listChannels.getChannelsList();

        ListChannelsRequest channelsRequest_single = ListChannelsRequest.newBuilder()
                .setPeer(ByteString.copyFromUtf8("024dad4a4294c2e544b25df4ff51fd9019a30f96663cddf4795bd5973461e2edb7")).build();
        lightningBlockingStub.listChannels(channelsRequest_single);



        ChanInfoRequest chanInfoRequest = ChanInfoRequest.newBuilder()
                .setChanId(282574488403969L)
//                .setChanId(172623325626369L)
                .build();
        ChannelEdge chanInfo = lightningBlockingStub.getChanInfo(chanInfoRequest);


        System.out.println();
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