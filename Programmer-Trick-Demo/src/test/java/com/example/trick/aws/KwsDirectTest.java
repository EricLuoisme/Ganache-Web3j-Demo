package com.example.trick.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Kws test
 *
 * @author Roylic
 * 2022/12/15
 */
public class KwsDirectTest {

    private final static String accessKey = "";
    private final static String secretKey = "";

    @Test
    public void test() {


        // create a client
        AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                .withRegion(Regions.AP_SOUTHEAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        // create key
        String desc = "Key for protecting critical data";
        CreateKeyRequest req = new CreateKeyRequest().withDescription(desc);
        CreateKeyResult result = kmsClient.createKey(req);

        String keyId = "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab";

        GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
        dataKeyRequest.setKeyId(keyId);
        dataKeyRequest.setKeySpec("AES_256");

        GenerateDataKeyResult dataKeyResult = kmsClient.generateDataKey(dataKeyRequest);
        ByteBuffer plaintextKey = dataKeyResult.getPlaintext();
        ByteBuffer encryptedKey = dataKeyResult.getCiphertextBlob();
    }

}
