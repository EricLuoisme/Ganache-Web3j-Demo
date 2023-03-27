package com.example.web3j.combination.solana;

import com.example.web3j.combination.solana.dto.AccountInfo;
import com.example.web3j.combination.solana.dto.SigResult;
import com.example.web3j.combination.solana.dto.extra.AssetChanging;
import com.example.web3j.combination.solana.dto.extra.SigPair;
import com.example.web3j.combination.solana.dto.extra.SigResultTask;
import com.example.web3j.combination.solana.dto.extra.TxnResultTask;
import com.example.web3j.combination.solana.handler.BalanceChangingHandler;
import com.example.web3j.combination.solana.utils.SolanaReqUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Roylic
 * 2023/3/24
 */
public class SolanaTxTest {


    private static final String ADDRESS = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);


    @Test
    public void getAllAssociatedTokenAccountFullTxn() throws JsonProcessingException {

        int topNum = 10;
        StopWatch stopWatch = new StopWatch("Get Account Related Transaction Full Details");
        ObjectMapper om = new ObjectMapper();

        String checkingAccount = "GQ6V9ZLVibN7eAtxEQxLJjXX8L9RybMJPpUCwi16vVgL";


        // 1. find all associated token accounts
        stopWatch.start("Get Associated Token Accounts");
        List<AccountInfo> accountInfos = SolanaReqUtil.rpcAssociatedTokenAccountByOwner(okHttpClient, checkingAccount);
        stopWatch.stop();
        System.out.println("Got all associated token accounts");
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(accountInfos));
        System.out.println("\n\n");


        // 2. find all related accounts signatures
        stopWatch.start("Get Account related Signatures");
        List<CompletableFuture<SigResultTask>> futureList = new LinkedList<>();
        accountInfos.forEach(accountInfo -> {
            CompletableFuture<SigResultTask> futureTask = CompletableFuture.supplyAsync(
                    () -> SigResultTask.builder()
                            .account(accountInfo.getPubkey())
                            .sigResultList(SolanaReqUtil.rpcAccountSignaturesWithLimit(okHttpClient, accountInfo.getPubkey(), topNum))
                            .build(),
                    executorService);
            futureList.add(futureTask);
        });
        // main account signatures
        CompletableFuture<SigResultTask> futureTask = CompletableFuture.supplyAsync(
                () -> SigResultTask.builder()
                        .account(ADDRESS)
                        .sigResultList(SolanaReqUtil.rpcAccountSignaturesWithLimit(okHttpClient, ADDRESS, topNum))
                        .build(),
                executorService);
        futureList.add(futureTask);
        // join
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        voidCompletableFuture.join();

        // 3. using set + queue to find latest signatures
        Set<String> signatureSet = new HashSet<>();
        Queue<SigPair> sigAccPairQueue = new PriorityQueue<>((a, b) -> (int) (b.getBlockDt() - a.getBlockDt()));
        futureList.stream().map(CompletableFuture::join)
                .forEach(sigResultTask -> sigResultTask.getSigResultList()
                        .forEach(sigResult -> {
                            boolean add = signatureSet.add(sigResult.getSignature());
                            if (add) {
                                sigAccPairQueue.add(
                                        SigPair.builder()
                                                .account(sigResultTask.getAccount())
                                                .signature(sigResult.getSignature())
                                                .blockDt(sigResult.getBlockTime())
                                                .build());
                            }
                        }));
        stopWatch.stop();
        System.out.println("Got all associated token accounts' signatures");
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(sigAccPairQueue));
        System.out.println("\n\n");


        // 4. for top 10 signature -> go find full transaction
        stopWatch.start("Get top n full transactions");
        Queue<TxnResultTask> txnResultQueue = new PriorityQueue<>(
                (a, b) -> (int) (b.getTxnResult().getBlockTime() - a.getTxnResult().getBlockTime()));
        List<CompletableFuture<TxnResultTask>> txnFutureList = new LinkedList<>();
        int i = 0;
        while (i++ < topNum) {
            SigPair sigPair = sigAccPairQueue.poll();
            CompletableFuture<TxnResultTask> txnResultFuture = CompletableFuture.supplyAsync(
                    TxnResultTask.builder()
                            .associatedTokenAccount(sigPair.getAccount())
                            .signature(sigPair.getSignature())
                            .txnResult(SolanaReqUtil.rpcTransactionBySignature(okHttpClient, sigPair.getSignature()))::build,
                    executorService);
            txnFutureList.add(txnResultFuture);
        }
        CompletableFuture<Void> anotherFut = CompletableFuture.allOf(txnFutureList.toArray(new CompletableFuture[0]));
        anotherFut.join();

        // cause txnResult would contain blockDt, just get() them and add them all into the priority queue
        for (CompletableFuture<TxnResultTask> txnFuture : txnFutureList) {
            try {
                TxnResultTask txnResultTask = txnFuture.get();
                txnResultQueue.add(txnResultTask);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        stopWatch.stop();
        System.out.println("Got all associated token accounts' transaction details");
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(txnResultQueue));
        System.out.println("\n\n");


        // 5. parsing the full txn -> what we needed
        stopWatch.start("Extract txn needed");
        List<TxnNeeded> txnNeededList = new LinkedList<>();
        while (!txnResultQueue.isEmpty()) {
            TxnResultTask txnResultTask = txnResultQueue.poll();
            Map<String, AssetChanging> assetDifMap = BalanceChangingHandler.getAssetDifInTxn(
                    txnResultTask.getSignature(), txnResultTask.getTxnResult());
            TxnNeeded txnNeeded = constructTxnNeeded(assetDifMap, ADDRESS,
                    txnResultTask.getAssociatedTokenAccount(), txnResultTask.getTxnResult().getBlockTime());
            txnNeededList.add(txnNeeded);
        }
        stopWatch.stop();
        System.out.println("Got all top txn");
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(txnNeededList));
        System.out.println("\n\n");

        System.out.println(stopWatch);
    }


    @Test
    public void getThisAccountFullTxn() throws Exception {
        int topNum = 10;
        StopWatch stopWatch = new StopWatch("Get Single Account Transaction Full Details");
        ObjectMapper om = new ObjectMapper();

        String checkingAccount = "GQ6V9ZLVibN7eAtxEQxLJjXX8L9RybMJPpUCwi16vVgL";


        // 1. Get account's signature with limit
        stopWatch.start("Get Account related Signatures");
        List<SigResult> sigResults = SolanaReqUtil.rpcAccountSignaturesWithLimit(okHttpClient, checkingAccount, topNum);
        Iterator<SigResult> iterator = sigResults.iterator();
        stopWatch.stop();

        // 2. Get Full Signature Details
        stopWatch.start("Get top n full transactions");
        int i = 0;
        List<CompletableFuture<TxnResultTask>> txnFutureList = new LinkedList<>();
        while (iterator.hasNext() && i++ < topNum) {
            SigResult sigResult = iterator.next();
            CompletableFuture<TxnResultTask> txnResultFuture = CompletableFuture.supplyAsync(
                    TxnResultTask.builder()
                            .associatedTokenAccount(checkingAccount)
                            .signature(sigResult.getSignature())
                            .txnResult(SolanaReqUtil.rpcTransactionBySignature(okHttpClient, sigResult.getSignature()))::build,
                    executorService);
            txnFutureList.add(txnResultFuture);
        }
        CompletableFuture<Void> anotherFut = CompletableFuture.allOf(txnFutureList.toArray(new CompletableFuture[0]));
        anotherFut.join();

        // cause txnResult would contain blockDt, just get() them and add them all into the priority queue
        Queue<TxnResultTask> txnResultQueue = new PriorityQueue<>(
                (a, b) -> (int) (b.getTxnResult().getBlockTime() - a.getTxnResult().getBlockTime()));
        for (CompletableFuture<TxnResultTask> txnFuture : txnFutureList) {
            try {
                TxnResultTask txnResultTask = txnFuture.get();
                txnResultQueue.add(txnResultTask);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        stopWatch.stop();
        System.out.println("Got accounts' transaction details");
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(txnResultQueue));
        System.out.println("\n\n");

        // 3. Construct return
        List<TxnNeeded> txnNeededList = new LinkedList<>();
        while (!txnResultQueue.isEmpty()) {
            TxnResultTask txnResultTask = txnResultQueue.poll();
            Map<String, AssetChanging> assetDifMap = BalanceChangingHandler.getAssetDifInTxn(
                    txnResultTask.getSignature(), txnResultTask.getTxnResult());
            TxnNeeded txnNeeded = constructTxnNeeded(assetDifMap, ADDRESS,
                    txnResultTask.getAssociatedTokenAccount(), txnResultTask.getTxnResult().getBlockTime());
            txnNeededList.add(txnNeeded);
        }
        stopWatch.start("Extract txn needed");

        System.out.println("Got all top txn");
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(txnNeededList));
        System.out.println("\n\n");

        System.out.println(stopWatch);
    }


    /**
     * get txn
     */
    private static TxnNeeded constructTxnNeeded(Map<String, AssetChanging> assetDifMap, String mainAddress, String associatedTokenAddress, Long blockDt) {
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

            String preSolBalanceStr = relatedAssetChange.getPreSolBalance().toString();
            String preRawTokenAmtStr = relatedAssetChange.getPreRawTokenAmt();
            String postSolBalanceStr = relatedAssetChange.getPostSolBalance().toString();
            String postRawTokenAmtStr = relatedAssetChange.getPostRawTokenAmt();

            BigInteger preBalance = relatedAssetChange.isSplTransfer()
                    ? StringUtils.hasLength(preRawTokenAmtStr) ? new BigInteger(preRawTokenAmtStr) : BigInteger.ZERO
                    : StringUtils.hasLength(preSolBalanceStr) ? new BigInteger(preSolBalanceStr) : BigInteger.ZERO;

            BigInteger postBalance = relatedAssetChange.isSplTransfer()
                    ? StringUtils.hasLength(postRawTokenAmtStr) ? new BigInteger(postRawTokenAmtStr) : BigInteger.ZERO
                    : StringUtils.hasLength(postSolBalanceStr) ? new BigInteger(postSolBalanceStr) : BigInteger.ZERO;

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
