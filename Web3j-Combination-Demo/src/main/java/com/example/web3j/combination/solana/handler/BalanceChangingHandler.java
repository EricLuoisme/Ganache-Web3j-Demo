package com.example.web3j.combination.solana.handler;

import com.example.web3j.combination.solana.dto.extra.AssetChanging;
import com.example.web3j.combination.solana.dto.extra.TokenBalanceDif;
import com.example.web3j.combination.solana.dto.InnerTxn;
import com.example.web3j.combination.solana.dto.Meta;
import com.example.web3j.combination.solana.dto.Txn;
import com.example.web3j.combination.solana.dto.TxnResult;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.web3j.combination.solana.constant.SolanaConstants.TOKEN_PROGRAM_ID;

/**
 * For helping easier getting stuff from Solana full resp block
 *
 * @author Roylic
 * 2023/2/2
 */
public final class BalanceChangingHandler {

    /**
     * Get all related address's asset changing under single Txn
     */
    public static Map<String, AssetChanging> getAssetDifInTxn(String txnHash, TxnResult txnResult) {
        List<String> accountKeys = txnResult.getTransaction().getMessage().getAccountKeys();
        Meta meta = txnResult.getMeta();
        Map<String, TokenBalanceDif> tokenBalanceChangingMap = fillAddressTokenBalanceChangingMap(accountKeys, meta, txnHash);
        return fillAddressBalanceChangingMap(accountKeys, meta, txnHash, tokenBalanceChangingMap);
    }


    /**
     * Get all related address's asset changing under single Txn
     */
    public static Map<String, AssetChanging> getAssetDifInTxn(Txn singleTxn) {
        InnerTxn transaction = singleTxn.getTransaction();
        Meta meta = singleTxn.getMeta();

        // prepare for common using
        String txnHash = transaction.getSignatures().get(0);
        List<String> accountKeys;
        if (null != transaction.getMessage()) {
            // full block info
            accountKeys = transaction.getMessage().getAccountKeys();
        } else if (null != transaction.getAccountKeys()) {
            // only accounts block info
            accountKeys = transaction.getAccountKeys().stream()
                    .map(InnerTxn.AccountBlockAccKeys::getPubkey)
                    .collect(Collectors.toList());
        } else {
            // ERROR
            return null;
        }

        // construct token balance map
        Map<String, TokenBalanceDif> tokenChangingMap = fillAddressTokenBalanceChangingMap(accountKeys, meta, txnHash);

        // construct balance map
        return fillAddressBalanceChangingMap(accountKeys, meta, txnHash, tokenChangingMap);
    }

    /**
     * Get Map(Address, TokenBalanceDif) from a single txn
     *
     * @param singleTxn Txn
     * @return Map<String, TokenBalanceDif>
     */
    public static Map<String, TokenBalanceDif> getAddressTokenBalanceMap(Txn singleTxn) {
        InnerTxn transaction = singleTxn.getTransaction();
        return fillAddressTokenBalanceChangingMap(transaction.getMessage().getAccountKeys(),
                singleTxn.getMeta(), transaction.getSignatures().get(0));
    }


    private static Map<String, TokenBalanceDif> fillAddressTokenBalanceChangingMap(List<String> accountKeys, Meta meta, String txnHash) {

        Map<String, TokenBalanceDif> tokenBalanceDifMap = new HashMap<>();

        // flat preTokenBalance & postTokenBalance
        Map<String, Meta.TokenBalance> preTokenBalanceMap = meta.getPreTokenBalances().stream()
                .collect(Collectors.toMap((tokenBalance) -> accountKeys.get(tokenBalance.getAccountIndex()), Function.identity()));

        Map<String, Meta.TokenBalance> postTokenBalanceMap = meta.getPostTokenBalances().stream()
                .collect(Collectors.toMap((tokenBalance) -> accountKeys.get(tokenBalance.getAccountIndex()), Function.identity()));

        accountKeys.forEach(account -> {

            if (account.equalsIgnoreCase(TOKEN_PROGRAM_ID)) {
                return;
            }

            Meta.TokenBalance preTokenBalance = preTokenBalanceMap.get(account);
            Meta.TokenBalance postTokenBalance = postTokenBalanceMap.get(account);
            if (null == preTokenBalance && null == postTokenBalance) {
                return;
            }
            TokenBalanceDif singleTokenBalanceDif = TokenBalanceDif.builder()
                    .owner(account)
                    .programId(null == preTokenBalance ? postTokenBalance.getProgramId() : preTokenBalance.getProgramId())
                    .preRawTokenAmt(null == preTokenBalance ? "" : preTokenBalance.getUiTokenAmount().getAmount())
                    .preTokenAmt(null == preTokenBalance ? "" : preTokenBalance.getUiTokenAmount().getUiAmountString())
                    .postRawTokenAmt(null == postTokenBalance ? "" : postTokenBalance.getUiTokenAmount().getAmount())
                    .postTokenAmt(null == postTokenBalance ? "" : postTokenBalance.getUiTokenAmount().getUiAmountString())
                    .tokenDecimal(null == preTokenBalance ? postTokenBalance.getUiTokenAmount().getDecimals() : preTokenBalance.getUiTokenAmount().getDecimals())
                    .txHash(txnHash)
                    .build();
            tokenBalanceDifMap.put(account, singleTokenBalanceDif);
        });

        return tokenBalanceDifMap;
    }

    private static Map<String, AssetChanging> fillAddressBalanceChangingMap(List<String> accountKeys, Meta meta, String txnHash, Map<String, TokenBalanceDif> tokenBalanceMap) {

        Map<String, AssetChanging> assetChangingMap = new HashMap<>();
        List<Long> preBalances = meta.getPreBalances();
        List<Long> postBalances = meta.getPostBalances();

        for (int i = 0; i < accountKeys.size(); i++) {
            String address = accountKeys.get(i);
            Long preBalance = preBalances.get(i);
            Long postBalance = postBalances.get(i);

            AssetChanging.AssetChangingBuilder builder = AssetChanging.builder()
                    .address(address)
                    .preSolBalance(preBalance)
                    .postSolBalance(postBalance)
                    .signature(txnHash)
                    .assetChangeEnum(preBalance.equals(postBalance) ? AssetChanging.ChangeEnum.STEADY :
                            (preBalance.compareTo(postBalance) > 0 ? AssetChanging.ChangeEnum.SOL_DEBIT : AssetChanging.ChangeEnum.SOL_CREDIT));


            TokenBalanceDif tokenBalanceDif = tokenBalanceMap.get(address);
            if (null != tokenBalanceDif) {
                String preRawTokenAmtStr = tokenBalanceDif.getPreRawTokenAmt();
                String postRawTokenAmtStr = tokenBalanceDif.getPostRawTokenAmt();
                BigInteger preRawTokenAmt = StringUtils.hasLength(preRawTokenAmtStr) ? new BigInteger(preRawTokenAmtStr) : BigInteger.ZERO;
                BigInteger postRawTokenAmt = StringUtils.hasLength(postRawTokenAmtStr) ? new BigInteger(postRawTokenAmtStr) : BigInteger.ZERO;

                int compareResult = preRawTokenAmt.compareTo(postRawTokenAmt);
                builder.splTransfer(true)
                        .owner(tokenBalanceDif.getOwner())
                        .programId(tokenBalanceDif.getProgramId())
                        .preRawTokenAmt(tokenBalanceDif.getPreRawTokenAmt())
                        .postRawTokenAmt(tokenBalanceDif.getPostRawTokenAmt())
                        .preTokenAmt(tokenBalanceDif.getPreTokenAmt())
                        .postTokenAmt(tokenBalanceDif.getPostTokenAmt())
                        .tokenDecimal(tokenBalanceDif.getTokenDecimal())
                        .assetChangeEnum(compareResult == 0 ? AssetChanging.ChangeEnum.STEADY :
                                (compareResult > 0 ? AssetChanging.ChangeEnum.SPL_DEBIT : AssetChanging.ChangeEnum.SPL_CREDIT));
            }

            assetChangingMap.put(address, builder.build());
        }

        return assetChangingMap;
    }


}
