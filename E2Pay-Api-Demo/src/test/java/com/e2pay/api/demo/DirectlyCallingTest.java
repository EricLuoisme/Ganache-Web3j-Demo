package com.e2pay.api.demo;

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class DirectlyCallingTest {

    private static final String baseUrl = "https://mobiletest.mbayar.co.id/switching";

    @Test
    public void md5Test() throws NoSuchAlgorithmException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String test = "123456";

        md5.update(test.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md5.digest();

        String s = DatatypeConverter.printHexBinary(digest).toUpperCase(Locale.ROOT);
        System.out.println(s);
    }

}
