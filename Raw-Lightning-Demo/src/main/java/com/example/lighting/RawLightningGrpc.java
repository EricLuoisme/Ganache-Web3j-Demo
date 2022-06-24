package com.example.lighting;

import com.example.lightning.Payment;
import com.example.lightning.router.RouterGrpc;
import com.example.lightning.router.TrackPaymentRequest;
import com.google.protobuf.ByteString;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.codec.binary.Hex;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Raw calling for lnd by grpc
 *
 * @author Roylic
 * 2022/6/23
 */
public class RawLightningGrpc {

    /**
     * Macaroon Credential
     */
    static class MacaroonCallCredential extends CallCredentials {

        private final String macaroon;

        MacaroonCallCredential(String macaroon) {
            this.macaroon = macaroon;
        }

        @Override
        public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
            executor.execute(() -> {
                try {
                    Metadata headers = new Metadata();
                    Metadata.Key<String> macaroonKey = Metadata.Key.of("macaroon", Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(macaroonKey, macaroon);
                    metadataApplier.apply(headers);
                } catch (Throwable e) {
                    metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                }
            });
        }

        @Override
        public void thisUsesUnstableApi() {
        }
    }

    private final static String POLAR_BASE_URL = "https://127.0.0.1:";
    private final static String POLAR_MACAROON_LOC = "/Users/pundix2022/.polar/networks/1/volumes/lnd";

    // Alice
    private final static int ALICE_GRPC_PORT = 10001;
    private final static String ALICE_REST_HOST = "https://127.0.0.1:8081";
    private final static String ALICE_CERT = POLAR_MACAROON_LOC + "/alice/tls.cert";
    private final static String ALICE_MACAROON = POLAR_MACAROON_LOC + "/alice/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ALICE_INVOICE_MACAROON = POLAR_MACAROON_LOC + "/alice/data/chain/bitcoin/regtest/invoice.macaroon";
    private final static String ALICE_PUB_KEY = "02c94f36b8574122ecf189a90fea84f02b8b80c21577f60499a2345ae9621c9fb4";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_REST_HOST = "https://127.0.0.1:8084";
    private final static String DAVE_CERT = POLAR_MACAROON_LOC + "/dave/tls.cert";
    private final static String DAVE_MACAROON = POLAR_MACAROON_LOC + "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "03b042a9d98111696f7aa00815dd8c740a55afb452bf89de33a3001ce8a03f8f65";

    // Erin
    private final static int ERIN_GRPC_PORT = 10005;
    private final static String ERIN_REST_HOST = "https://127.0.0.1:8085";
    private final static String ERIN_CERT = POLAR_MACAROON_LOC + "/erin/tls.cert";
    private final static String ERIN_MACAROON = POLAR_MACAROON_LOC + "/erin/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ERIN_PUB_KEY = "034236bdc8b2b4d5ad0c3292364d514f5fc12008172531d6fe48853553a9948a7b";


    /**
     * Only payment has been pay (settled) from my node, could be found in this track payment request
     */
    public static void main(String... args) throws IOException {
        SslContext sslContext = GrpcSslContexts.forClient().trustManager(new File(ALICE_CERT)).build();
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress("127.0.0.1", ALICE_GRPC_PORT);
        ManagedChannel channel = channelBuilder.sslContext(sslContext).build();

        String macaroon =
                Hex.encodeHexString(
                        Files.readAllBytes(Paths.get(ALICE_MACAROON))
                );


        RouterGrpc.RouterBlockingStub routerBlockingStub = RouterGrpc.
                newBlockingStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));

        byte[] bytes = Numeric.hexStringToByteArray("42dfa58e4137db25ccbd6397bd16e79791ba53bccd6ba2543c14eeee1763b049");


        TrackPaymentRequest req = TrackPaymentRequest.newBuilder().setPaymentHash(ByteString.copyFrom(bytes)).build();

        Iterator<Payment> paymentIterator = routerBlockingStub.trackPaymentV2(req);

        while (paymentIterator.hasNext()) {
            Payment next = paymentIterator.next();
            System.out.println(paymentIterator.next().toString());
        }


    }
}
