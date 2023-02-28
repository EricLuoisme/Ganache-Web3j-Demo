package com.example.web3j.combination.signature;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * ECC 椭圆加密算法相关
 *
 * @author Roylic
 * 2022/5/16
 */
public class EccRelatedTest {

    public static final BigInteger PRI_KEY_BIT = new BigInteger("11143367460393672249027881867686059798241873017515514375134825408887879305524");

    public static final String PUB_KEY_HEX_BIT = "03b4ea618ec1f12b90bab57c02e5ac81b9d0af188e7c5abb676baca7d65585c665";

    public static final BigInteger PRI_KEY_ETH = new BigInteger("7597642418811430721017447471407646827753973391384087387872450773144584345619");

    public static final String PUB_KEY_ADDRESS = "0x4fabaf87ed2e76ef18229eecd827e5ce6f074120";

    // 不支持空格等特殊符号
    public static final String MESSAGE = "WhatTheFuckIsThat";


    @Test
    public void SignAndVerifyTest_ByBitcoinMethod() {

        // create ECKey with pri + pub keys
        ECKey ecKeyFull = ECKey.fromPrivateAndPrecalculatedPublic(PRI_KEY_BIT.toByteArray(), Utils.HEX.decode(PUB_KEY_HEX_BIT));

        // convert string to hex
        String hexMsg = Hex.toHexString(MESSAGE.getBytes(StandardCharsets.UTF_8));
        System.out.println("\n >>> Hex Msg is : " + hexMsg + "\n");

        // Step 1 : Sign
        byte[] hashMsg = Sha256Hash.hash(hexMsg.getBytes());
        ECKey.ECDSASignature signed = ecKeyFull.sign(Sha256Hash.wrap(hashMsg));
        // 0 - 33
        byte[] bytes_r = signed.r.toByteArray();
        // 32 - rest
        byte[] bytes_s = signed.s.toByteArray();
        // new Byte
        byte[] signedBytes = new byte[64];
        System.arraycopy(bytes_r, 1, signedBytes, 0, 32);
        System.arraycopy(bytes_s, 0, signedBytes, 32, 32);
        String encodedMsgBase64 = Base64.getEncoder().encodeToString(signedBytes);
        System.out.println("\n >>> Encoded Msg in Base64: " + encodedMsgBase64 + "\n");


        // Step 2 : Verify
        byte[] source = Base64.getDecoder().decode(encodedMsgBase64);
        for (int i = signedBytes.length - 1; i >= 0; i--) {
            if (source[i] != signedBytes[i]) {
                System.out.println(">>> not equal on index:" + i);
            }
        }

        // For pub key, we need to make sure which platform we are using now
        // Bitcoin: pub key with encryption
        // Ethereum: pub key without encryption
        byte[] sBytes = new byte[32];
        byte[] rBytes = new byte[33];
        System.arraycopy(source, 32, sBytes, 0, 32);
        System.arraycopy(source, 0, rBytes, 1, 32);
        ECKey.ECDSASignature ecdsaSignature = new ECKey.ECDSASignature(new BigInteger(rBytes), new BigInteger(sBytes));

        // Step 3 : Run
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // create ECKey entity by only adding public key
        ECKey ecKeyPubOnly = ECKey.fromPublicOnly(Utils.HEX.decode(PUB_KEY_HEX_BIT));
        boolean verify_pub = ecKeyPubOnly.verify(Sha256Hash.wrap(hashMsg), ecdsaSignature);
        stopWatch.stop();
        System.out.println(">>> Verifying using pub with result: " + verify_pub + ", used: " + stopWatch.getTotalTimeSeconds());

        stopWatch.start();
        // create ECKey entity that only can verify the signature, by using public key point
        ECPoint pubKeyPoint = ecKeyFull.getPubKeyPoint();
        ECKey ecKeyVerifyOnly = ECKey.fromPublicOnly(pubKeyPoint, false);
        boolean verify_pub_point = ecKeyVerifyOnly.verify(Sha256Hash.wrap(hashMsg), ecdsaSignature);
        stopWatch.stop();
        System.out.println(">>> Verifying using pub point with result: " + verify_pub_point + ", used: " + stopWatch.getTotalTimeSeconds());

    }

    @Test
    public void SignAndVerifyTest_ByEthereumMethod() throws Exception {

//        ECKeyPair ecKeyPair = Credentials.create("priKey").getEcKeyPair();
        ECKeyPair ecKeyPair = ECKeyPair.create(PRI_KEY_ETH);

        // convert string to hex
        String hexMsg = Hex.toHexString(MESSAGE.getBytes(StandardCharsets.UTF_8));
        System.out.println("\n >>> Hex Msg is : " + hexMsg + "\n");

        // 1. use web3j methods to sign the message
        Sign.SignatureData signatureData = Sign.signMessage(Hash.sha3(hexMsg.getBytes(StandardCharsets.UTF_8)), ecKeyPair);
        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        byte[] v = signatureData.getV();


        System.out.println(">>> Signature r: " + Numeric.toHexString(r) + "\n with length: " + Numeric.toHexString(r).length());
        System.out.println(">>> Signature s: " + Numeric.toHexString(s) + "\n with length: " + Numeric.toHexString(s).length());
        System.out.println(">>> Signature v: " + Numeric.toHexString(v) + "\n with length: " + Numeric.toHexString(v).length());
        System.out.println();

        // combine into single signature
        StringBuilder sb = new StringBuilder();
        sb.append(Numeric.toHexString(r).substring(2));
        sb.append(Numeric.toHexString(s).substring(2));
        sb.append(Numeric.toHexString(v).substring(2));
        System.out.println(">>> Single combined signature would be: " + sb);

        // 2. verify the message
        String inputSignature = sb.toString();
        String r_str = "0x" + inputSignature.substring(0, 64);
        String s_str = "0x" + inputSignature.substring(64, 64 + 64);
        String v_str = "0x" + inputSignature.substring(64 + 64);

        byte[] bytes_r = Numeric.hexStringToByteArray(r_str);
        byte[] bytes_s = Numeric.hexStringToByteArray(s_str);
        byte[] bytes_v = Numeric.hexStringToByteArray(v_str);


        // For Ethereum, it's also using the ECDSA Algorithm for calculating and verifying the signature

    }

    /**
     * Creat a random ECC pair
     */
    private void createNewEccPair_ByBitcoinMethod() {
        ECKey ecKey = new ECKey();
        BigInteger priKey = ecKey.getPrivKey();
        byte[] pubKey = ecKey.getPubKey();
        System.out.println(">>> Private key: " + priKey.toString());
        System.out.println(">>> Public key in Hex: " + Utils.HEX.encode(pubKey));
    }

    private void creatNewECKeyPair_ByEthereumMethod() throws Exception {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        BigInteger priKey = ecKeyPair.getPrivateKey();

        WalletFile walletFile = Wallet.createLight("123456", ecKeyPair);
        String address = walletFile.getAddress();
        System.out.println(">>> Private key: " + priKey.toString());
        System.out.println(">>> Public key of address: " + "0x" + address);
    }
}
