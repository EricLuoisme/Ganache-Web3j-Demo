package com.example.web3j.combination.solana;

import com.example.web3j.combination.solana.dto.AccountInfo;
import com.example.web3j.combination.solana.dto.TxnResult;
import com.example.web3j.combination.solana.dto.extra.AssetChanging;
import com.example.web3j.combination.solana.dto.extra.SigPair;
import com.example.web3j.combination.solana.dto.extra.SigResultTask;
import com.example.web3j.combination.solana.utils.SolanaReqUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    @Test
    public void getAccountTxn() {

        // 1. find all associated token accounts
        List<AccountInfo> accountInfos = SolanaReqUtil.rpcAssociatedTokenAccountByOwner(okHttpClient, ADDRESS);

        // 2. find all related accounts signatures
        List<CompletableFuture<SigResultTask>> futureList = new LinkedList<>();
        accountInfos.forEach(accountInfo -> {
            CompletableFuture<SigResultTask> futureTask = CompletableFuture.supplyAsync(
                    () -> getAccountSignatureTask(okHttpClient, accountInfo.getPubkey(), 10), executorService);
            futureList.add(futureTask);
        });
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

        // 4. TODO for top 10 signature -> go find full transaction
        Queue<TxnResult> txnResultQueue = new PriorityQueue<>((a, b) -> (int) (b.getBlockTime() - a.getBlockTime()));
        int i = 0;
        while (i++ < 10) {
            SigPair peek = sigAccPairQueue.peek();
            CompletableFuture.supplyAsync(() -> SolanaReqUtil.rpcTransactionBySignature(okHttpClient, peek.getSignature()))
        }


    }


    private SigResultTask getAccountSignatureTask(OkHttpClient okHttpClient, String account, int num) {
        return SigResultTask.builder()
                .account(account)
                .sigResultList(SolanaReqUtil.rpcAccountSignaturesWithLimit(okHttpClient, account, num))
                .build();
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
