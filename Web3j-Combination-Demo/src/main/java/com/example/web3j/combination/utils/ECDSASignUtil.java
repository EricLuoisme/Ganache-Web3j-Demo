package com.example.web3j.combination.utils;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

/**
 * Sign Util
 *
 * @author Roylic
 * 2023/3/2
 */
public class ECDSASignUtil {

    /**
     * Create signature in Base64 Hex
     */
    public static String[] sign(String sortedJsonStr, ECKeyPair signPriKey) {
        // sort & encode
        String[] rsv = new String[3];
        String plainHexTxt = Hex.toHexString(sortedJsonStr.getBytes(StandardCharsets.UTF_8));
        byte[] sha3PlainHexTxt = Hash.sha3(plainHexTxt.getBytes(StandardCharsets.UTF_8));
        // sign
        Sign.SignatureData signatureData = Sign.signMessage(sha3PlainHexTxt, signPriKey);
        rsv[0] = Numeric.toHexString(signatureData.getR()).substring(2);
        rsv[1] = Numeric.toHexString(signatureData.getS()).substring(2);
        rsv[2] = Numeric.toHexString(signatureData.getV()).substring(2);
        return rsv;
    }

    /**
     * Verify signature
     */
    public static boolean verify(String sortedJsonStr, String hexR, String hexS, String hexV, String signPubKey) {
        // reconstruct signature
        byte[] bytes_r = Numeric.hexStringToByteArray("0x" + hexR);
        byte[] bytes_s = Numeric.hexStringToByteArray("0x" + hexS);
        byte[] bytes_v = Numeric.hexStringToByteArray("0x" + hexV);
        Sign.SignatureData reconstructSigData = new Sign.SignatureData(bytes_v, bytes_r, bytes_s);
        // sort & encode
        String plainHexTxt = Hex.toHexString(sortedJsonStr.getBytes(StandardCharsets.UTF_8));
        byte[] sha3PlainHexTxt = Hash.sha3(plainHexTxt.getBytes(StandardCharsets.UTF_8));
        // verify
        try {
            String address = Keys.getAddress(Sign.signedMessageToKey(sha3PlainHexTxt, reconstructSigData));
            return signPubKey.equalsIgnoreCase("0x" + address);
        } catch (SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

}
