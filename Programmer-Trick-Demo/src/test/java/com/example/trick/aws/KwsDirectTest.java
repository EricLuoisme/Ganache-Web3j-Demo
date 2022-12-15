package com.example.trick.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.*;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Kws test
 *
 * @author Roylic
 * 2022/12/15
 */
public class KwsDirectTest {

    private final static String accessKey = "";
    private final static String secretKey = "";

    private final static String keyId = "";
    private final static String keyArn = "";

    // create a client
    private final static AWSKMS kmsClient = AWSKMSClientBuilder.standard()
            .withRegion(Regions.AP_SOUTHEAST_1)
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
            .build();

    /**
     * Generate Main Key
     */
    @Test
    public void generateMainKey() {
        String desc = "Key for local test of KMS";
        CreateKeyRequest req = new CreateKeyRequest().withDescription(desc);
        CreateKeyResult result = kmsClient.createKey(req);
        System.out.println("Key Id: " + result.getKeyMetadata().getKeyId());
        System.out.println("Arn: " + result.getKeyMetadata().getArn());
    }


    @Test
    public void useDataKey() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        // A. Generate Encrypted-Data-Key
        ByteBuffer encryptedDataKey = generateDataKey();

        // B. Decrypt Encrypted-Data-Key
        ByteBuffer decryptedDataKey = decryptDataKey(encryptedDataKey.array());

        // C. encryption
        String cipherText = encryptByAESDataKey(decryptedDataKey, "Hello World");
        System.out.println("Cipher Text: " + cipherText);

        // D. description
        String plainText = decryptByAESDataKey(decryptedDataKey, cipherText);
        System.out.println("Plain Text: " + plainText);
    }

    /**
     * Encrypt plain text with decryptedDataKey
     */
    private String encryptByAESDataKey(ByteBuffer decryptedDataKey, String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(decryptedDataKey.array(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Decrypt cipher text with decryptedDataKey
     */
    private String decryptByAESDataKey(ByteBuffer decryptedDataKey, String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(decryptedDataKey.array(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
    }

    /**
     * Decode DataKey
     */
    private ByteBuffer decryptDataKey(byte[] ciphertextBytes) {
        // convert to byteBuffer
        ByteBuffer encryptedDataKey = ByteBuffer.wrap(ciphertextBytes);
        DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(encryptedDataKey);
        DecryptResult decryptResult = kmsClient.decrypt(decryptRequest);
        // return Decoded-Data-Key
        return decryptResult.getPlaintext();
    }


    /**
     * Generate DataKey, once, store the response
     */
    private ByteBuffer generateDataKey() {
        // set key & algorithm
        GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
        dataKeyRequest.setKeyId(keyId);
        dataKeyRequest.setKeySpec("AES_256");
        // request
        GenerateDataKeyResult dataKeyResult = kmsClient.generateDataKey(dataKeyRequest);
        ByteBuffer plaintextKey = dataKeyResult.getPlaintext();
        ByteBuffer ciphertextBlob = dataKeyResult.getCiphertextBlob();
        System.out.println("plainTextKey: " + plaintextKey);
        System.out.println("ciphertextBlob: " + ciphertextBlob);
        System.out.println();
        System.out.println("Data Key Id: " + dataKeyResult.getKeyId());
        // cipherBlob is the credential we need to store into db, aka. Encrypted-Data-Key
        return ciphertextBlob;
    }


}
