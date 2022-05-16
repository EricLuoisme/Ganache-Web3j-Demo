package com.example.ganacheweb3jdemo;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.SignatureDecodeException;
import org.bitcoinj.core.Utils;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

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

    public static final BigInteger PRI_KEY = new BigInteger("11143367460393672249027881867686059798241873017515514375134825408887879305524");

    public static final String PUB_KEY_HEX = "03b4ea618ec1f12b90bab57c02e5ac81b9d0af188e7c5abb676baca7d65585c665";

    public static final String MESSAGE = "Whattx";


    @Test
    public void SignAndVerifyTest() throws SignatureDecodeException {

        // create ECKey with pri + pub keys
        ECKey ecKeyFull = ECKey.fromPrivateAndPrecalculatedPublic(PRI_KEY.toByteArray(), Utils.HEX.decode(PUB_KEY_HEX));

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
        ECKey ecKeyPubOnly = ECKey.fromPublicOnly(Utils.HEX.decode(PUB_KEY_HEX));
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

    /**
     * Creat a random ECC pair
     */
    private void createNewEccPair() {
        ECKey ecKey = new ECKey();
        BigInteger priKey = ecKey.getPrivKey();
        byte[] pubKey = ecKey.getPubKey();
        System.out.println(">>> Private key: " + priKey.toString());
        System.out.println(">>> Public key: " + Utils.HEX.encode(pubKey));
    }
}
