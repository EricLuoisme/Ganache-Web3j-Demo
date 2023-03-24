package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.web3j.combination.solana.dto.AccountInfo;
import com.example.web3j.combination.solana.dto.Meta;
import com.example.web3j.combination.solana.dto.SignatureWithStatus;
import com.example.web3j.combination.solana.dto.SingleTxn;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getSignaturesForAddress\",\"params\":[\"%s\",{\"limit\":10}]}";

        // dispatch
        List<AccountInfo> accountInfos = JSON.parseArray(accountInfosJsonStr, AccountInfo.class);
        List<CompletableFuture<String>> futureList = new LinkedList<>();
        accountInfos.forEach(accountInfo -> {
            CompletableFuture<String> callFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return callAndPrint(String.format(req, accountInfo.getPubkey()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });
            futureList.add(callFuture);
        });

        // join
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        voidCompletableFuture.join();

        // combine all signature
        Set<String> signatureSet = new HashSet<>();
        futureList.forEach(stringCompletableFuture -> {
            try {
                String s = stringCompletableFuture.get();
                JSONObject jsonObject = JSONObject.parseObject(s);
                JSONArray result = jsonObject.getJSONArray("result");
                List<SignatureWithStatus> signatureList = JSON.parseArray(result.toJSONString(), SignatureWithStatus.class);
                signatureSet.addAll(signatureList.stream().map(SignatureWithStatus::getSignature).collect(Collectors.toList()));

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        System.out.println(JSON.toJSONString(signatureSet));
        System.out.println();
    }

    // 3. get transaction details
    @Test
    public void getTop10Transaction() throws IOException {

        String req = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTransaction\",\"params\":[\"%s\",{\"encoding\":\"json\"}]}";


//        List<SignatureWithStatus> signatureWithStatuses = JSON.parseArray(signatureSet, SignatureWithStatus.class);
//        SignatureWithStatus signatureWithStatus = signatureWithStatuses.get(0);
        String s = callAndPrint(String.format(req, "2FjiGVncyv1SWpGsYVx2yYegUdipTtgWFMnU6kfjZVZF69Y2afyh6GJ6eLofjhkUSxCpdudJiqdLJbU7haynyugC"));
        JSONObject jsonObject = JSONObject.parseObject(s);
        SingleTxn singleTxn = JSON.parseObject(jsonObject.getJSONObject("result").toJSONString(), SingleTxn.class);

        // parse txn
        int mainAddIdx = -1;
        int tokenAddIdx = -1;
        List<String> accountKeys = singleTxn.getTransaction().getMessage().getAccountKeys();

        // judge for idx
        int curIdx = 0;
        Iterator<String> iterator = accountKeys.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if ("AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D".equalsIgnoreCase(next)) {
                mainAddIdx = curIdx;
                continue;
            }
            if ("Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc".equalsIgnoreCase(next)) {
                tokenAddIdx = curIdx;
                continue;
            }
            if (mainAddIdx > 0 && tokenAddIdx > 0) {
                break;
            }
            curIdx++;
        }

        String preAmt = "", postAmt = "";
        String contractAddress = "";
        String relatedAccount = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";
        String couterAccount = "";
        int decimal = 9; // default

        if (mainAddIdx >= 0 && tokenAddIdx < 0) {
            // SOL transfer
            List<Long> preBalances = singleTxn.getMeta().getPreBalances();
            List<Long> postBalances = singleTxn.getMeta().getPostBalances();
            preAmt = preBalances.get(mainAddIdx).toString();
            postAmt = postBalances.get(mainAddIdx).toString();
            couterAccount = singleTxn.getTransaction().getMessage().getAccountKeys().get(0);

        } else if (tokenAddIdx >= 0) {
            // SPL transfer
            List<Meta.TokenBalance> preTokenBalances = singleTxn.getMeta().getPreTokenBalances();
            List<Meta.TokenBalance> postTokenBalances = singleTxn.getMeta().getPostTokenBalances();
            preAmt = preTokenBalances.size() == 0 ? "0" : preTokenBalances.get(tokenAddIdx).getUiTokenAmount().getAmount();
            postAmt = postTokenBalances.size() == 0 ? "0" : postTokenBalances.get(tokenAddIdx).getUiTokenAmount().getAmount();

            decimal = preTokenBalances.size() == 0 ? postTokenBalances.get(tokenAddIdx).getUiTokenAmount().getDecimals()
                    : preTokenBalances.get(tokenAddIdx).getUiTokenAmount().getDecimals();
            contractAddress = preTokenBalances.size() == 0 ? postTokenBalances.get(tokenAddIdx).getMint()
                    : preTokenBalances.get(tokenAddIdx).getMint();
            relatedAccount = "Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc";
        }

        TxnNeeded txnNeeded = TxnNeeded.parseSingleTxn(singleTxn, "5kehV53LHjE32EEDVwQkELeQj31zjFe31twxSuHmGkSrMhBEHjdMLhsv5mA8v7BdAFR3FAtCDjTLV8qnvSnUEcRS",
                relatedAccount, preAmt, postAmt, contractAddress, decimal);
        System.out.println("\n\n");
        ObjectMapper om = new ObjectMapper();
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(txnNeeded));
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
        private String unit; // 币种名称
        private String transactionHash; // 交易Hash
        private String contractAddress; // 合约地址
        private String method; // 方法名称
        private String methodId; // 交易类型
        private String fromAddress; // 所属地址
        private String toAddress; // 所属地址
        private String amount = "0.0"; // 交易金额
        private String fee = "0.0"; // 交易手续费
        private long blockDt; // 出块时间(交易时间)
        private String content; // 中间填充字段
        //        private List<AddressInfo> addressInfos; // BTC 地址信息列表
        private boolean isCrossChain; // 是否为跨链，true为跨链，false为普通交易
        private Integer sourceChainStatus; // 跨链发起链状态, 1:等待, 2:成功，3:失败
        private Integer targetChainStatus; // 跨链接收链状态, 1:等待, 2:成功，3:失败
        private Integer crossChainId; // 跨链所属 链(例如：0:ethereum-mainnet, 1:bitcoin-mainnet, 20:ethereum-kovan 21:bitcoin-testnet 23:fx-core-testnet)
        private String crossTransactionHash = ""; // 跨链所属 交易Hash
        private String crossAmount = "0.0"; // 跨链所属 交易金额
        private String crossBridgeFee = "0.0"; // 跨链所属 fx pundix交易手续费
        private Long crossBlockDt; // 跨链所属 出块时间(交易时间)
        private String nftTokenId; // NFT所属 tokenId
        private String nftName; // NFT所属 名称
        private String nftSmallImage; // NFT所属 缩略图
        private String validatorSrc; // 委托所属 转出验证人
        private String validatorDst; // 委托所属 转入验证人
        private String gasUnit; // margin X 手续费单位
        private String tradingPair; // margin X 交易对
        private String browserTxHashUrl; // 关联链的txHash浏览器Url
        private String crossBrowserTxHashUrl; // 跨链的txHash浏览器Url

        private int decimal;


        private static final String devNetUrl = "https://solscan.io/tx/%s?cluster=devnet";


        public static TxnNeeded parseSingleTxn(SingleTxn txn, String relatedSignature, String relatedAccount,
                                               String preAmt, String postAmt, String contractAddress, int decimal) {

            TxnNeededBuilder builder = TxnNeeded.builder();
            BigInteger dif = new BigInteger(preAmt).subtract(new BigInteger(postAmt));
            if (dif.compareTo(BigInteger.ZERO) > 0) {
                builder = builder.toAddress(relatedAccount).type(0);
            } else {
                builder = builder.fromAddress(relatedAccount).type(1);
            }

            return builder
                    .chainId(2004)
                    .transactionHash(relatedSignature)
                    .contractAddress(contractAddress)
                    .amount(dif.abs().toString())
                    .blockDt(txn.getBlockTime())
                    .decimal(decimal)
                    .browserTxHashUrl(String.format(devNetUrl, relatedSignature))
                    .build();
        }
    }

}
