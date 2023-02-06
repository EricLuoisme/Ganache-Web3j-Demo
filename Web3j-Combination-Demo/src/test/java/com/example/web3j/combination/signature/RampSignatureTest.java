package com.example.web3j.combination.signature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;


/**
 * https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html
 */
public class RampSignatureTest {

    private static final String RAMP_SIGNATURE
            = "MEUCIQC84Q2baimqldkUcuu5Cwf/0+XUyk3si6NAg0TmX3cG2gIgDByYTevGbdtfFu+jbgE/kcrp5bWq3kFnvHImZD+gL98=";

    private static final String RAMP_BODY
            = "{\"type\":\"CREATED\",\"purchase\":{\"endTime\":\"2023-02-08T06:33:45.177Z\",\"cryptoAmount\":\"8092335106789142\",\"fiatCurrency\":\"GBP\",\"fiatValue\":11,\"assetExchangeRateEur\":1541.1240336212973,\"fiatExchangeRateEur\":1.1460004584001833,\"baseRampFee\":0.10882449504950495,\"networkFee\":0.008726000000000001,\"appliedFee\":0.11755049504950495,\"createdAt\":\"2023-02-05T06:33:45.252Z\",\"updatedAt\":\"2023-02-05T06:33:46.959Z\",\"id\":\"svqxncpttecdxh9\",\"asset\":{\"address\":\"0xA6FA4fB5f76172d178d61B04b0ecd319C5d1C0aa\",\"symbol\":\"MATIC_ETH\",\"apiV3Symbol\":\"ETH\",\"name\":\"WETH on Polygon Mumbai testnet\",\"decimals\":18,\"type\":\"MATIC_ERC20\",\"apiV3Type\":\"ERC20\",\"chain\":\"MATIC\"},\"receiverAddress\":\"0x36f0a040c8e60974d1f34b316b3e956f509db7e5\",\"assetExchangeRate\":1344.7848317379442,\"purchaseViewToken\":\"gxueyqe4ucptub44\",\"status\":\"INITIALIZED\",\"paymentMethodType\":\"MANUAL_BANK_TRANSFER\"}}";

    private static final String RAMP_PUB_KEY
            = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEevN2PMEeIaaMkS4VIfXOqsLebj19kVeuwWl0AnkIA6DJU0r3ixkXVhJTltycJtkDoEAYtPHfARyTofB5ZNw9xA==";

    @Test
    public void signatureVerify_RAMP() throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException, SignatureException, JsonProcessingException {

        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");

        KeyFactory kf = KeyFactory.getInstance("EC");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(RAMP_PUB_KEY));
        PublicKey publicKey = kf.generatePublic(publicKeySpec);
        ecdsaVerify.initVerify(publicKey);

        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        HashMap mapVal = om.readValue(RAMP_BODY, HashMap.class);

        ecdsaVerify.update(om.writeValueAsString(mapVal).getBytes(StandardCharsets.UTF_8));
        boolean verify = ecdsaVerify.verify(Base64.getDecoder().decode(RAMP_SIGNATURE));
        System.out.println(verify);

    }

}
