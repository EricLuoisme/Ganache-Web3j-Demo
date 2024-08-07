package com.example.web3j.combination.solana.sdk;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solana.custom.utils.atom.ByteUtils;
import com.solana.custom.utils.atom.PublicKey;
import okhttp3.*;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Roylic
 * 2023/3/22
 */
public class SolanaAccountDataDecoding {


    private static final String SOLANA_DEV_URL = HttpUrl.parse("https://solana-devnet.g.alchemy.com/v2/On35d8LdFc1QGYD-wCporecGj359qian").newBuilder().build().toString();
    private static final MediaType mediaType = MediaType.parse("application/json");
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();


    private static final String METAPLEX_TOKEN_META_PROGRAM_ID = "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s";


    @Test
    public void getTokenPubKey() throws Exception {

//        String nftMintAccount = "EZqtsCxYpYtNaX1Pd2ep3ZUVxS6qHLVQriugvbKGEahk"; // nft
        String nftMintAccount = "ARHE7qXefr79DqyApiEkZ2QwnyzfAnUew4jRXfkMBVT2"; // nft

        // 1. get nft token meta account pub key
        PublicKey.ProgramDerivedAddress derivedAddress = PublicKey.findProgramAddress(
                Arrays.asList(
                        "metadata".getBytes(StandardCharsets.UTF_8),
                        new PublicKey(METAPLEX_TOKEN_META_PROGRAM_ID).toByteArray(),
                        new PublicKey(nftMintAccount).toByteArray()),
                new PublicKey(METAPLEX_TOKEN_META_PROGRAM_ID));
        System.out.println(derivedAddress.getAddress());

        // 2. get nft token meta account info
        String getAccountInfo = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getAccountInfo\",\"params\":[\"" + derivedAddress.getAddress() + "\",{\"encoding\":\"base64\"}]}";
        callAndPrint(getAccountInfo);
    }


    @Test
    public void tryDecodeMetapluxData() {
        String base64DataStr = "BJFmu4q8Nqbfbjctj5+IGdJqz1GNfBTwx+ef1OIIT5lEyZHNApSQhhH4+13jqVLCWl2buaAfO1onpJALNxFgpBcgAAAATnVtYmVyICMwMDA1AAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAATkIAAAAAAAAAAMgAAABodHRwczovL2Fyd2VhdmUubmV0L2ZOWjhRaWJzd05UbjZPSXhNOGZIMFZwcFFhYzBtdGsyVk5HMlVET3ptLUUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJYAAQIAAABwR1rHRLcV9Pgwql6fuU9yLlnguuhQosZbVaLFO1SDZwEAkWa7irw2pt9uNy2Pn4gZ0mrPUY18FPDH55/U4ghPmUQAZAEBAf8BAAEBi/F3/535aQiLpyuFs9WhGNyT1kiOX782bZ4mKJOp0wUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
//        String base64DataStr = "BJFmu4q8Nqbfbjctj5+IGdJqz1GNfBTwx+ef1OIIT5lEi/F3/535aQiLpyuFs9WhGNyT1kiOX782bZ4mKJOp0wUgAAAATnVtYmVycyBDb2xsZWN0aW9uAAAAAAAAAAAAAAAAAAAKAAAATkIAAAAAAAAAAMgAAABodHRwczovL2Fyd2VhdmUubmV0L3NxRjM4Y0tvSnkwX2NXSmwwNGRIT09fazJGUnVpSVkxNm9pRzhiaW9qMFUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEAAACRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRAFkAAEB/wEAAAABAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
//        String base64DataStr = "6Sg5VQll/9TWSsqvRtRd9zGOW09XyQxIfWBiXYKbg3uRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRMC26lcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        byte[] decode = Base64.decode(base64DataStr);

        if (decode.length < 320) {
            // < 320 must be pure token
            return;
        }

        byte[] key = new byte[1];
        byte[] updateAuthority = new byte[32];
        byte[] mint = new byte[32];
        byte[] name = new byte[36];
        byte[] symbol = new byte[14];
        byte[] uri = new byte[204];
        byte[] sellerFeeBasicPoints = new byte[2];

        // need to distinguish pure token address (mint-> 1111111111, system program)

        System.arraycopy(decode, 0, key, 0, 1);
        System.arraycopy(decode, 1, updateAuthority, 0, 32);
        System.arraycopy(decode, 33, mint, 0, 32);
        System.arraycopy(decode, 65, name, 0, 36);
        System.arraycopy(decode, 101, symbol, 0, 14);
        System.arraycopy(decode, 115, uri, 0, 204);
        System.arraycopy(decode, 319, sellerFeeBasicPoints, 0, 2);

        System.out.println("Key: " + Numeric.toHexString(key));
        System.out.println("Update Authority: " + Base58.encode(updateAuthority));
        System.out.println("Mint: " + Base58.encode(mint));

        byte[] nameLenB = new byte[4];
        System.arraycopy(name, 0, nameLenB, 0, 4);
        int nameLen = ByteBuffer.wrap(nameLenB).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] trimName = trimRight(name);
        System.out.println("Name: " + new String(trimName, 4, trimName.length - 4, StandardCharsets.UTF_8));

        byte[] trimSymbol = trimRight(symbol);
        System.out.println("Symbol: " + new String(trimSymbol, 4, trimSymbol.length - 4, StandardCharsets.UTF_8));

        byte[] trimUri = trimRight(uri);
        System.out.println("Uri: " + new String(trimUri, 4, trimUri.length - 4, StandardCharsets.UTF_8));

        int unsignedInt = 0;
        for (int i = 0; i < sellerFeeBasicPoints.length; i++) {
            unsignedInt |= (sellerFeeBasicPoints[i] & 0xFF) << (8 * i);
        }
        System.out.println("Seller Fee Basic Points: " + unsignedInt);


        byte[] creatorIndicator = new byte[1];
        System.arraycopy(decode, 321, creatorIndicator, 0, 1);
        int decodeIdx = 322;


        if (creatorIndicator[0] == 1) {
            // have creator list
            byte[] creatorList = new byte[4];
            System.arraycopy(decode, decodeIdx, creatorList, 0, 4);
            int creatorNums = 0;
            for (int i = 0; i < creatorList.length; i++) {
                creatorNums |= (creatorList[i] & 0xFF) << (8 * i);
            }

            // traverse creators
            decodeIdx += 4;
            int i = 0;
            while (i++ < creatorNums) {
                byte[] creator = new byte[32];
                byte[] verified = new byte[1];
                byte[] shared = new byte[1];
                System.arraycopy(decode, decodeIdx, creator, 0, 32);
                System.arraycopy(decode, decodeIdx + 32, verified, 0, 1);
                System.arraycopy(decode, decodeIdx + 32 + 1, shared, 0, 1);
                decodeIdx += 32 + 1 + 1;

                System.out.println("Creator_" + i);
                System.out.println("  Address: " + Base58.encode(creator));
                System.out.println("  Verified: " + (verified[0] == 1 ? "true" : "false"));
                System.out.println("  Share: " + shared[0]);
            }
        }

        byte[] primarySale = new byte[1];
        byte[] isMutable = new byte[1];
        System.arraycopy(decode, decodeIdx++, primarySale, 0, 1);
        System.arraycopy(decode, decodeIdx++, isMutable, 0, 1);
        System.out.println("Primary Sale Happened: " + (primarySale[0] == 1 ? "true" : "false"));
        System.out.println("Is Mutable: " + (isMutable[0] == 1 ? "true" : "false"));


        // edition nonce 396
        byte[] editionNonceIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, editionNonceIndicator, 0, 1);
        if (editionNonceIndicator[0] == 1) {
            byte[] editionNonce = new byte[1];
            System.arraycopy(decode, decodeIdx++, editionNonce, 0, 1);
            System.out.println("Edition Nonce: " + (editionNonce[0] & 0xff));
        }

        // token standard
        byte[] tokenStandardIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, tokenStandardIndicator, 0, 1);
        if (tokenStandardIndicator[0] == 1) {
            byte[] tokenStandard = new byte[1];
            System.arraycopy(decode, decodeIdx++, tokenStandard, 0, 1);
            System.out.println("Token Standard: " + new BigInteger(tokenStandard));
        }

        // collection
        byte[] collectionIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, collectionIndicator, 0, 1);
        if (collectionIndicator[0] == 1) {
            byte[] verified = new byte[1];
            byte[] collectionMintKey = new byte[32];
            System.arraycopy(decode, decodeIdx++, verified, 0, 1);
            System.arraycopy(decode, decodeIdx, collectionMintKey, 0, 32);
            decodeIdx += 32;
            System.out.println("Collection: ");
            System.out.println("  Verified: " + (verified[0] == 1 ? "true" : "false"));
            System.out.println("  Nft Collection Mint Account: " + Base58.encode(collectionMintKey));
        }

        // uses
        byte[] usesIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, usesIndicator, 0, 1);
        if (usesIndicator[0] == 1) {
            byte[] useMethod = new byte[1];
            byte[] remaining = new byte[8];
            byte[] total = new byte[8];
            System.arraycopy(decode, decodeIdx++, useMethod, 0, 1);
            System.arraycopy(decode, decodeIdx, remaining, 0, 8);
            System.arraycopy(decode, decodeIdx + 8, total, 0, 8);
            decodeIdx += 8 + 8;
            int remainingNums = 0;
            for (int i = 0; i < remaining.length; i++) {
                remainingNums |= (remaining[i] & 0xFF) << (8 * i);
            }
            int totalNums = 0;
            for (int i = 0; i < total.length; i++) {
                totalNums |= (total[i] & 0xFF) << (8 * i);
            }
            System.out.println("Uses: ");
            System.out.println("  Use Method: " + new BigInteger(useMethod));
            System.out.println("  Remaining: " + remainingNums);
            System.out.println("  Total: " + totalNums);
        }

        // programmable config
        byte[] programIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, programIndicator, 0, 1);
        if (programIndicator[0] == 1) {
            byte[] programConfig = new byte[33];
            System.arraycopy(decode, decodeIdx, programConfig, 0, 33);
            System.out.println();
        }


        System.out.println();
    }


    @Test
    public void tryDecodeTokenData() {
        String base64DataStr = "6Sg5VQll/9TWSsqvRtRd9zGOW09XyQxIfWBiXYKbg3uRZruKvDam3243LY+fiBnSas9RjXwU8Mfnn9TiCE+ZRMC26lcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        byte[] decode = Base64.decode(base64DataStr);

        if (decode.length > 320) {
            // 320 must be MetaPlux -> Nft token
            return;
        }

        int decodeIdx = 0;

        byte[] mint = new byte[32];
        byte[] owner = new byte[32];
        byte[] amount = new byte[8];
        System.arraycopy(decode, decodeIdx, mint, 0, 32);
        System.arraycopy(decode, decodeIdx + 32, owner, 0, 32);
        System.arraycopy(decode, decodeIdx + 32 + 32, amount, 0, 8);
        System.out.println("Mint: " + Base58.encode(mint));
        System.out.println("Owner: " + Base58.encode(owner));

//        long amountNum = 0;
//        for (int i = 0; i < amount.length; i++) {
//            amountNum |= (long) (amount[i] & 0xFF) << (8 * i);
//        }
//        System.out.println("Amount: " + amountNum);
        // need to read bytes & reverse it then put it into bigInteger
        BigInteger bigInteger = ByteUtils.readUint64(amount, 0);
        System.out.println("Amount: " + bigInteger);

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

    public static byte[] trimRight(byte[] array) {
        int i = array.length - 1;
        while (array[i] == 0 && i >= 0) {
            i--;
        }
        byte[] trimArr = new byte[i + 1];
        System.arraycopy(array, 0, trimArr, 0, i + 1);
        return trimArr;
    }

}
