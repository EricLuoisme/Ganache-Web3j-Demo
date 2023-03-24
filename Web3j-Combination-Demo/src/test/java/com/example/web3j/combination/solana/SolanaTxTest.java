package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.solana.dto.AccountInfo;
import com.example.web3j.combination.solana.dto.AssetChanging;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.web3j.combination.solana.SolanaConstants.TOKEN_PROGRAM_ID;

/**
 * @author Roylic
 * 2023/3/24
 */
public class SolanaTxTest {

    //    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();
    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://api.devnet.solana.com").newBuilder().build().toString();

    private static final MediaType mediaType = MediaType.parse("application/json");

    private static final String ADDRESS = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);


    private static final String accountInfosJsonStr = "[{\"account\":{\"data\":[\"yZHNApSQhhH4+13jqVLCWl2buaAfO1onpJALNxFgpBeRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"9ewpuaKZr7RXuepL2Jt5WrjVauwSEFQv1ENr7K4XHbc5\"},{\"account\":{\"data\":[\"kh8Fs8Y4tJ231JOM+lli6H+BkqKWIhptkYmWm1Sw3jORZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"AcB9mBTdCALue7sqjGDwpkdnMGurReX6hWf7nGyB9MoX\"},{\"account\":{\"data\":[\"i/F3/535aQiLpyuFs9WhGNyT1kiOX782bZ4mKJOp0wWRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"GhR8u95gTcro3JntFsBjCtt4sHY89KXzAXKq1TU97ssn\"},{\"account\":{\"data\":[\"0UOYfN/O466aUdqPO1RfF1w3IT6x+4OLIwi+dvDL8QWRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"8M1QhLSfoTazWqoEnGZzhErxwLbEwrztTKMSkpixF18j\"},{\"account\":{\"data\":[\"6Sg5VQll/9TWSsqvRtRd9zGOW09XyQxIfWBiXYKbg3uRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZREA7CVgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc\"},{\"account\":{\"data\":[\"N5A5qqmH4H2QR6Ntr19HvAjht2Ku60WSBndJJhy4L4yRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"4gMkxCVvyR91EpYMBv2tk7u3k3wSS2X7kCpjz51pGt7u\"},{\"account\":{\"data\":[\"+FjPK0iHhnq4lhJGf7+Y5ydSitD/YeuEm5rTsi55vUWRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"9Eci6JDuBPCADUtahSk4e9QU3fPfXkDeA5dvZ5T2aiMm\"},{\"account\":{\"data\":[\"9ZavWHMLea32cDrIIZ2eGjmqbqMi0I8gbEAdbVnz8j6RZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"AgMTqic3oRt1wWypn1Cg89ck66pxocNpX8z7JsdwKWGT\"},{\"account\":{\"data\":[\"bbSGkyAbW7a0PKck+yy+GDPYDeuaWINdwKZGhz33GTyRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"base64\"],\"executable\":false,\"lamports\":2039280,\"owner\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\",\"rentEpoch\":0},\"pubkey\":\"62pH4kwbnKBa3KbfPCZoviaKLXmTQWLZB4cdPWsv2mrH\"}]";

    private static final String signatureSet = "[\"5kehV53LHjE32EEDVwQkELeQj31zjFe31twxSuHmGkSrMhBEHjdMLhsv5mA8v7BdAFR3FAtCDjTLV8qnvSnUEcRS\",\"5iSiSD9h1jCqxUezx52izUEUN9jZDbK3hvota8wgsH8dPNqnHhmpYj4dQMSTqr5svfdRwzUXcZqT7mSDnQNufCi\",\"643qxSzxEp3MUXeUhCDkSPJQVp7keATzY24C8ysNwZzTnJFCfzrRoGD8ezYkzRPGU6VLJYjoLnYvFYMkLcfYR3Te\",\"3ZeumisWXthziPcJXQnjXFGcrhu35aeAak3summaRmJkLQtPy9eQ8rVyyXY5hBgdGFj1KEKgc4r5cjKSBe6p2pc8\",\"3HWBkXSxysXrxJHgFbmvnXkxL9tk66EKGBhMrZAwMyciRsHc8ycoTDRtjJDu4ehTt9wcCPBRrLXtzY6DmiwuEkyA\",\"5hjEExdgXo7qQYF9i5BCiug2ZqoexmeRuVKJn1AmMbZJH49UQUHfMoGierYMSuYFPGYUhrVmyRg3G1LLAnv6Acee\",\"2FjiGVncyv1SWpGsYVx2yYegUdipTtgWFMnU6kfjZVZF69Y2afyh6GJ6eLofjhkUSxCpdudJiqdLJbU7haynyugC\",\"5hCZ5NL54nhfMfKqJco2p7ZXyPoszhwjDigGNFgMNjkgdhB5v9x8SztqNNbU3fqd35NNQuacJznrrr7LYeA4mbpe\",\"3U1FWqZWcJgbxpCy4gkDfeq7VvuJ2xZ5hsFWXZG1SGWTcctE1kRqpGFBHcSusRj8GTfBwLHWchhZ9UmPyYcDNFnw\",\"24QqmcUxcDNpfB6n1FPzGcqm897Bqd3GTBQgAW9LTF2FRuD2CvwZ4hFBetb1UpJfsnKKM2oTxwDQUxZn2GrrGJFj\",\"4txz77pkDN9wJH34JPKqZXW2SwmGBsF4e6DwK3TzN9ufcsDctmjvLJatmeHRBZjEEYQeJXTp3NWYVgNbv819sb3w\",\"5T2KkZ9fH9J1qbeTS1MhXVYjDfXoGVrAJrYrVEPmrLoSiUzn6xqGS264tphX6xCnN8zWF4ZrRkBphGfimExpEPQd\",\"agcRGHbkyuyCPcsGphPNiG5W7DKKJ1cnGUqwfNKGbLGWXFg8p99pxDHvcGeqeysxGayb7PUEFiZCHk3KFkaF5St\",\"5FzwrKYLcduqyxEFMvfEXDZTEoB9j9qBDfYT1p92s7HiFHdZ74qGoEDbdZ12Z2xW3HrLhTsNwrs2xutyXdDPEaeS\",\"DbLjXdwcYgyMHQHRDHqNSTE6Fu5GeQNfqsBjVVT2B5EEjuVD6kWxgFSSsAoiLnZAeSDXNzvKXZyVybved2mtiZs\"]";


    // 1. find token accounts by their owner
    @Test
    public void getAssociatedAccountsByOwnerAccount() throws IOException {
        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTokenAccountsByOwner\",\"params\":[\"" + ADDRESS + "\",{\"programId\":\"" + TOKEN_PROGRAM_ID + "\"},{\"encoding\":\"base64\"}]}";
        String resp = callAndPrint(req);
        JSONObject jsonObject = JSONObject.parseObject(resp);
        JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("value");
        List<AccountInfo> accountInfos = JSON.parseArray(jsonArray.toJSONString(), AccountInfo.class);
        System.out.println(JSON.toJSON(accountInfos));
    }

    // 2. request for all sig related to the account
    @Test
    public void getTop10SignatureByAccounts() {
//        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getSignaturesForAddress\",\"params\":[\"%s\",{\"limit\":10}]}";
//
//        // dispatch
//        List<AccountInfo> accountInfos = JSON.parseArray(accountInfosJsonStr, AccountInfo.class);
//        List<CompletableFuture<Pair<String, String>>> futureList = new LinkedList<>();
//        accountInfos.forEach(accountInfo -> {
//            CompletableFuture<Pair<String, String>> callFuture = CompletableFuture.supplyAsync(() -> {
//                try {
//                    return getAccountSignatures(accountInfo.getPubkey());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            });
//            futureList.add(callFuture);
//        });
//        // join
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
//        voidCompletableFuture.join();
//
//        // combine all signature
//        Map<String, String> signatureSet = new HashMap<>();
//        futureList.forEach(stringCompletableFuture -> {
//            try {
//                String s = stringCompletableFuture.get();
//                JSONObject jsonObject = JSONObject.parseObject(s);
//                JSONArray result = jsonObject.getJSONArray("result");
//                List<SignatureWithStatus> signatureList = JSON.parseArray(result.toJSONString(), SignatureWithStatus.class);
//                signatureSet.putAll(signatureList.stream().collect(Collectors.toMap(signatureList)));
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        });
//
//        System.out.println(JSON.toJSONString(signatureSet));
//        System.out.println();
    }


    @Test
    public void totalProcess() throws IOException {
//
//        // 1. find all associated accounts
//        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTokenAccountsByOwner\",\"params\":[\"" + ADDRESS + "\",{\"programId\":\"" + TOKEN_PROGRAM_ID + "\"},{\"encoding\":\"base64\"}]}";
//        String resp = callAndPrint(req);
//        JSONObject jsonObject = JSONObject.parseObject(resp);
//        JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("value");
//        List<AccountInfo> accountInfos = JSON.parseArray(jsonArray.toJSONString(), AccountInfo.class);
//
//        // 2. dispatch to find all txn history
//        List<CompletableFuture<String>> futureList = new LinkedList<>();
//        accountInfos.forEach(accountInfo -> {
//            CompletableFuture<String> callFuture = CompletableFuture.supplyAsync(() -> {
//                try {
//                    return callAndPrint(String.format(req, accountInfo.getPubkey()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            });
//            futureList.add(callFuture);
//        });
//        futureList.add(CompletableFuture.supplyAsync())
//
//        // join
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
//        voidCompletableFuture.join();
//
//        // combine all signature
//        Map<String, String> signatureSet = new HashMap<>();
//        futureList.forEach(stringCompletableFuture -> {
//            try {
//                String s = stringCompletableFuture.get();
//                JSONObject jsonObject = JSONObject.parseObject(s);
//                JSONArray result = jsonObject.getJSONArray("result");
//                List<SignatureWithStatus> signatureList = JSON.parseArray(result.toJSONString(), SignatureWithStatus.class);
//                signatureSet.putAll(signatureList.stream().collect(Collectors.toMap(signatureList)));
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        });

    }


    private TxnNeeded constructTxnNeeded(Map<String, AssetChanging> assetDifMap, String mainAddress, String associatedTokenAddress, Long blockDt) {

        AssetChanging mainAsset = assetDifMap.get(mainAddress);
        AssetChanging ataAsset = assetDifMap.get(associatedTokenAddress);

        // must be credit
        if (mainAsset == null) {
            String counterAccount = "";
            for (Map.Entry<String, AssetChanging> entry : assetDifMap.entrySet()) {
                if (entry.getValue().getAssetChangeEnum().equals(AssetChanging.ChangeEnum.SOL_DEBIT)) {
                    counterAccount = entry.getKey();
                    break;
                }
            }
            return TxnNeeded.parseSingleTxn(ataAsset, associatedTokenAddress, counterAccount, blockDt);
        }
        // must be debited
        String counterAccount = "";
        if (null == ataAsset) {
            // sol
            for (Map.Entry<String, AssetChanging> entry : assetDifMap.entrySet()) {
                if (entry.getValue().getAssetChangeEnum().equals(AssetChanging.ChangeEnum.SPL_CREDIT)) {
                    counterAccount = entry.getKey();
                    break;
                }
            }

        } else {
            // spl
            for (Map.Entry<String, AssetChanging> entry : assetDifMap.entrySet()) {
                if (entry.getValue().getAssetChangeEnum().equals(AssetChanging.ChangeEnum.SOL_CREDIT)) {
                    counterAccount = entry.getKey();
                    break;
                }
            }
        }
        return TxnNeeded.parseSingleTxn(null == ataAsset ? mainAsset : ataAsset, associatedTokenAddress, counterAccount, blockDt);
    }


    private static Pair<String, String> getAccountSignatures(String account) throws IOException {
        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getSignaturesForAddress\",\"params\":[\"%s\",{\"limit\":10}]}";
        return new Pair<>(account, callAndPrint(String.format(req, account)));
    }


    private static String callAndPrint(String jsonMsg) throws IOException {
        RequestBody body = RequestBody.create(jsonMsg, mediaType);
        Request request = new Request.Builder()
                .url(SOLANA_DEV_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        ObjectMapper om = new ObjectMapper();
        String str = om.writerWithDefaultPrettyPrinter().writeValueAsString(JSON.parse(response.body().string()));
        System.out.println(str);
        return str;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TxnNeeded {
        private String id;
        private int chainId;
        private int type; // 推送类型(0:入账; 1:出账;)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String unit; // 币种名称
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String transactionHash; // 交易Hash
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String contractAddress; // 合约地址
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String method; // 方法名称
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String methodId; // 交易类型
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String fromAddress; // 所属地址
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String toAddress; // 所属地址
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String amount = "0.0"; // 交易金额
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String fee = "0.0"; // 交易手续费
        private long blockDt; // 出块时间(交易时间)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String content; // 中间填充字段
        //        private List<AddressInfo> addressInfos; // BTC 地址信息列表
        private boolean isCrossChain; // 是否为跨链，true为跨链，false为普通交易
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Integer sourceChainStatus; // 跨链发起链状态, 1:等待, 2:成功，3:失败
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Integer targetChainStatus; // 跨链接收链状态, 1:等待, 2:成功，3:失败
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Integer crossChainId; // 跨链所属 链(例如：0:ethereum-mainnet, 1:bitcoin-mainnet, 20:ethereum-kovan 21:bitcoin-testnet 23:fx-core-testnet)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String crossTransactionHash = ""; // 跨链所属 交易Hash
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String crossAmount = "0.0"; // 跨链所属 交易金额
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String crossBridgeFee = "0.0"; // 跨链所属 fx pundix交易手续费
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Long crossBlockDt; // 跨链所属 出块时间(交易时间)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String nftTokenId; // NFT所属 tokenId
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String nftName; // NFT所属 名称
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String nftSmallImage; // NFT所属 缩略图
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String validatorSrc; // 委托所属 转出验证人
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String validatorDst; // 委托所属 转入验证人
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String gasUnit; // margin X 手续费单位
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String tradingPair; // margin X 交易对
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String browserTxHashUrl; // 关联链的txHash浏览器Url
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String crossBrowserTxHashUrl; // 跨链的txHash浏览器Url

        private int decimal;


        private static final String devNetUrl = "https://solscan.io/tx/%s?cluster=devnet";


        public static TxnNeeded parseSingleTxn(AssetChanging relatedAssetChange, String relatedAccount, String counterAccount, Long blockDt) {

            BigInteger preBalance = new BigInteger(relatedAssetChange.isSplTransfer() ? relatedAssetChange.getPreRawTokenAmt() : relatedAssetChange.getPreSolBalance().toString());
            BigInteger postBalance = new BigInteger(relatedAssetChange.isSplTransfer() ? relatedAssetChange.getPostRawTokenAmt() : relatedAssetChange.getPostSolBalance().toString());
            BigInteger difBalance = postBalance.subtract(preBalance);

            return TxnNeeded.builder()
                    .id(relatedAssetChange.getSignature())
                    .chainId(2004)
                    .type(difBalance.compareTo(BigInteger.ZERO) > 0 ? 0 : 1)
                    .transactionHash(relatedAssetChange.getSignature())
                    .method(relatedAssetChange.isSplTransfer() ? "SPL Transfer" : "SOL Transfer")
                    .fromAddress(difBalance.compareTo(BigInteger.ZERO) > 0 ? counterAccount : relatedAccount)
                    .toAddress(difBalance.compareTo(BigInteger.ZERO) > 0 ? relatedAccount : counterAccount)
                    .amount(difBalance.abs().toString())
                    .blockDt(blockDt)
                    .browserTxHashUrl(String.format(devNetUrl, relatedAssetChange.getSignature()))
                    .decimal(relatedAssetChange.isSplTransfer() ? relatedAssetChange.getTokenDecimal() : 9)
                    .build();
        }
    }

}
