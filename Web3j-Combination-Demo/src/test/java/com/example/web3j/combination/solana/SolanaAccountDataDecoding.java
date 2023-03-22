package com.example.web3j.combination.solana;

import com.alibaba.fastjson2.JSON;
import com.example.web3j.combination.solana.utils.PublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
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

        String nftAssociatedAccount = "EZqtsCxYpYtNaX1Pd2ep3ZUVxS6qHLVQriugvbKGEahk";

        // 1. get nft token meta account pub key
        PublicKey.ProgramDerivedAddress derivedAddress = PublicKey.findProgramAddress(
                Arrays.asList(
                        "metadata".getBytes(StandardCharsets.UTF_8),
                        new PublicKey(METAPLEX_TOKEN_META_PROGRAM_ID).toByteArray(),
                        new PublicKey(nftAssociatedAccount).toByteArray()),
                new PublicKey(METAPLEX_TOKEN_META_PROGRAM_ID));
        System.out.println(derivedAddress.getAddress());

        // 2. get nft token meta account info
        String getAccountInfo = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getAccountInfo\",\"params\":[\"" + derivedAddress.getAddress() + "\",{\"encoding\":\"base64\"}]}";
        callAndPrint(getAccountInfo);
    }


    @Test
    public void tryDecodeData() {
        String base58DataStr = "BJFmu4q8Nqbfbjctj5+IGdJqz1GNfBTwx+ef1OIIT5lEyZHNApSQhhH4+13jqVLCWl2buaAfO1onpJALNxFgpBcgAAAATnVtYmVyICMwMDA1AAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAATkIAAAAAAAAAAMgAAABodHRwczovL2Fyd2VhdmUubmV0L2ZOWjhRaWJzd05UbjZPSXhNOGZIMFZwcFFhYzBtdGsyVk5HMlVET3ptLUUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJYAAQIAAABwR1rHRLcV9Pgwql6fuU9yLlnguuhQosZbVaLFO1SDZwEAkWa7irw2pt9uNy2Pn4gZ0mrPUY18FPDH55/U4ghPmUQAZAEBAf8BAAEBi/F3/535aQiLpyuFs9WhGNyT1kiOX782bZ4mKJOp0wUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        byte[] decode = Base64.decode(base58DataStr);

        byte[] key = new byte[1];
        byte[] updateAuthority = new byte[32];
        byte[] mint = new byte[32];
        byte[] name = new byte[36];
        byte[] symbol = new byte[14];
        byte[] uri = new byte[204];
        byte[] sellerFeeBasicPoints = new byte[2];

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
