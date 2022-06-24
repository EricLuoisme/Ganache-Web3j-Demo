package com.example.lighting;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;

import java.util.concurrent.Executor;

/**
 * General using credential holder for Lightning-Grpc
 *
 * @author Roylic
 * 2022/6/24
 */
public class MacaroonCallCredential extends CallCredentials {

    private final String macaroon;


    public MacaroonCallCredential(String macaroon) {
        this.macaroon = macaroon;
    }

    /**
     * Auto intercept the request with macaroon heading added
     */
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
        // should put nothing
    }
}
