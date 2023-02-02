package com.example.web3j.combination.solana.fullBlock;

import com.example.web3j.combination.solana.dto.AssetChanging;
import com.example.web3j.combination.solana.dto.TokenBalanceDif;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * For helping easier getting stuff from Solana full resp block
 *
 * @author Roylic
 * 2023/2/2
 */
public final class FullBlockDecHandler {

    /**
     * Get all related address's asset changing under single Txn
     */
    public static Map<String, AssetChanging> getAssetDifInTxn(Txn singleTxn) {
        InnerTxn transaction = singleTxn.getTransaction();
        Meta meta = singleTxn.getMeta();

        // prepare for common using
        List<String> accountKeys = transaction.getMessage().getAccountKeys();
        String txnHash = transaction.getSignatures().get(0);

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
                .collect(Collectors.toMap(Meta.TokenBalance::getOwner, Function.identity()));

        Map<String, Meta.TokenBalance> postTokenBalanceMap = meta.getPostTokenBalances().stream()
                .collect(Collectors.toMap(Meta.TokenBalance::getOwner, Function.identity()));

        accountKeys.forEach(account -> {
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
            TokenBalanceDif tokenBalanceDif = tokenBalanceMap.get(address);
            AssetChanging assetChanging = AssetChanging.builder()
                    .address(address)
                    .preBalance(preBalances.get(i))
                    .postBalance(postBalances.get(i))
                    .tokenBalanceDif(tokenBalanceDif)
                    .txHash(txnHash)
                    .build();
            assetChangingMap.put(address, assetChanging);
        }

        return assetChangingMap;
    }


}
