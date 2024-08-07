package com.example.web3j.combination.solana.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solana.custom.dto.AccountInfo;
import com.solana.custom.dto.extra.AccountInfoFlat;
import com.solana.custom.dto.metaplex.MetaplexStandardJsonObj;
import com.solana.custom.handler.AccInfoDecodeHandler;
import com.solana.custom.utils.req.SolanaRequestUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.solana.custom.utils.atom.ByteUtils.trimRight;

/**
 * @author Roylic
 * 2023/3/24
 */
public class SolanaNftTest {


    private static final String WALLET_ACCOUNT = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private static final String SOL_DEV = "https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian";

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);


    @Test
    public void decodeAllNft() throws Exception {

        StopWatch stopWatch = new StopWatch("Decoding all associated nft token account's meta info");
        ObjectMapper om = new ObjectMapper();

        String checkingAccount = WALLET_ACCOUNT;

        // 1. find all associated token accounts
        stopWatch.start("Get Associated Token Accounts");
        List<AccountInfo> accountInfos = SolanaRequestUtil.rpcAssociatedTokenAccountByOwner(okHttpClient, SOL_DEV, checkingAccount);
        stopWatch.stop();
        System.out.println("Got all associated token accounts");
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(accountInfos));
        System.out.println("\n\n");

        // 2. derive nft pub-key address
        stopWatch.start("Filter & Derive Nft-Pub account");
        List<AccountInfoFlat> accountInfoFlatList = accountInfos.parallelStream()
                .map(AccInfoDecodeHandler::parseFlat)
                .filter(accountInfoFlat -> accountInfoFlat.getAmount() > 0)
                .collect(Collectors.toList());
        Map<String, String> atAddressDerivedAddressMap = accountInfoFlatList.parallelStream().collect(
                Collectors.toMap(AccountInfoFlat::getAtAddress,
                        accountInfoFlat -> AccInfoDecodeHandler.deriveNftTokenAddress(accountInfoFlat.getMintAddress())));
        stopWatch.stop();

        // 3. request for account info
        stopWatch.start("Decode & Request for NFT img url");
        List<CompletableFuture<NftFileItem>> futureList = new LinkedList<>();
        atAddressDerivedAddressMap.forEach((key, value) -> {
            CompletableFuture<NftFileItem> nftFileItemCompletableFuture = CompletableFuture.supplyAsync(
                    () -> constructNftFileTask(key, value), executorService);
            futureList.add(nftFileItemCompletableFuture);
        });
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        voidCompletableFuture.join();

        List<NftFileItem> nftFileItemList = new LinkedList<>();
        futureList.parallelStream().forEach(singleFuture -> {
            try {
                NftFileItem nftFileItem = singleFuture.get();
                if (StringUtils.hasLength(nftFileItem.getName())) {
                    nftFileItemList.add(nftFileItem);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        stopWatch.stop();

        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(nftFileItemList));
        System.out.println(stopWatch);
    }

    private NftFileItem constructNftFileTask(String nftAstAccount, String nftMintAccount) {
        Optional<String> opDataBase64 = SolanaRequestUtil.rpcAccountInfoDataBase64(okHttpClient, SOL_DEV, nftMintAccount);
        if (!opDataBase64.isPresent()) {
            return NftFileItem.builder().build();
        }

        String dataBase64 = opDataBase64.get();
        if (!StringUtils.hasLength(dataBase64)) {
            return NftFileItem.builder().build();
        }
        byte[] decode = Base64.decode(dataBase64);
        // pure token
        if (decode.length < 320) {
            return NftFileItem.builder().build();
        }
        // nft
        byte[] uri = new byte[204];
        System.arraycopy(decode, 115, uri, 0, 204);
        byte[] trimUri = trimRight(uri);
        if (trimUri.length < 4) {
            return NftFileItem.builder().build();
        }
        String uriStr = new String(trimUri, 4, trimUri.length - 4, StandardCharsets.UTF_8);
        MetaplexStandardJsonObj metaplexStandardJsonObj = SolanaRequestUtil.metaplexExternalJsonReq(okHttpClient, uriStr).get();
        // construct
        return NftFileItem.builder()
                .contractAddress(nftMintAccount)
                .tokenId(nftAstAccount)
                .name(metaplexStandardJsonObj.getName())
                .uri(uriStr)
                .fileType(0)
                .file(metaplexStandardJsonObj.getImage())
                .smallImage(metaplexStandardJsonObj.getImage())
                .description(metaplexStandardJsonObj.getDescription())
                .build();
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NftFileItem {
        private String contractAddress;
        private String tokenId;
        private String name;
        private String uri;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Integer blockHeight;
        private String file;
        private int fileType;
        private String smallImage;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Long nftId;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Integer eip;
        private String description;
    }


}
