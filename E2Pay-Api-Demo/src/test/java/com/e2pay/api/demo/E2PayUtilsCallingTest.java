package com.e2pay.api.demo;

import com.e2pay.demo.E2PayReqUtils;
import org.junit.Test;

/**
 * E2PayReqUtils directly calling test
 *
 * @author Roylic
 * 2022/11/8
 */
public class E2PayUtilsCallingTest {

    private static final int RETRY_TIMES = 1;


    @Test
    public void getCustomerAuthCode() {
        String userPhone = "082140988853";
        String md5Pwd = "E10ADC3949BA59ABBE56E057F20F883E";
        String callingResult = E2PayReqUtils.getCustomerAuthCodeByJSON(userPhone, md5Pwd, RETRY_TIMES);
        System.out.println(callingResult);
    }

    @Test
    public void getCustomerAuthToken() {
        String userAuthCode = "ODg5OGVhNjA0NmU1NGU3MWE5ZjMyMzA3M2EyMzY3MzU6MDA6MTY5NTc2ZTcxZDYzNGI0ZDg0M2IzMTcxZDE4NWJkMGVHU1JtQjZwb1QzSjNXSXVlWndlbFNTeWRiMkl6NzIyQw==";
        String callingResult = E2PayReqUtils.getCustomerAuthTokenByForm(userAuthCode, RETRY_TIMES);
        System.out.println(callingResult);
    }

    @Test
    public void getCustomerInfo() {
        String access_token = "VTBvd1YyWGdFUklyZmtVR0UwR2U2MDl4R1d1S0ZFMXQ6MDE6MTBhNmFlNWNkNDY5NDY3MDg4NGRlMTQzYTY1YzVhMWRYb2dieEE5Y1dOa2Rzb3JBWXVPTjFzYmE5V1Q0OENXcQ==";
        String customerInfo = E2PayReqUtils.getCustomerInfo(access_token, RETRY_TIMES);
        System.out.println(customerInfo);
    }

    @Test
    public void getCustomerLimit() {
        String access_token = "VTBvd1YyWGdFUklyZmtVR0UwR2U2MDl4R1d1S0ZFMXQ6MDE6MTBhNmFlNWNkNDY5NDY3MDg4NGRlMTQzYTY1YzVhMWRYb2dieEE5Y1dOa2Rzb3JBWXVPTjFzYmE5V1Q0OENXcQ==";
        String accountId = "8451182140988853";
        String customerLimit = E2PayReqUtils.getCustomerLimit(access_token, accountId, RETRY_TIMES);
        System.out.println(customerLimit);
    }

    @Test
    public void inquiryQRIS() {
        String access_token = "VTBvd1YyWGdFUklyZmtVR0UwR2U2MDl4R1d1S0ZFMXQ6MDE6MTBhNmFlNWNkNDY5NDY3MDg4NGRlMTQzYTY1YzVhMWRYb2dieEE5Y1dOa2Rzb3JBWXVPTjFzYmE5V1Q0OENXcQ==";
        String qrCode = "00020101021126680016ID.CO.CONTOH.WWW01189360082901234568900215MIDCONTOHACQ1230303UKE520411115303360550203570500.035802ID5913NamaMerchant16009NamaKota1610925441234162080704210163049140";
        String qrisResp = E2PayReqUtils.inquiryQRIS(access_token, qrCode, RETRY_TIMES);
        System.out.println(qrisResp);
    }

    @Test
    public void payTheORIS() {
        String access_token = "VTBvd1YyWGdFUklyZmtVR0UwR2U2MDl4R1d1S0ZFMXQ6MDE6MTBhNmFlNWNkNDY5NDY3MDg4NGRlMTQzYTY1YzVhMWRYb2dieEE5Y1dOa2Rzb3JBWXVPTjFzYmE5V1Q0OENXcQ==";
        String inquiryId = "012700081122512";
        String md5Pwd = "E10ADC3949BA59ABBE56E057F20F883E";
        double paymentAmt = 10.37;
        double paymentTip = 0;
        String localDebitId = "DEBIT_ORDER_LOCAL_3";
        String paymentResp = E2PayReqUtils.payQRIS(access_token, inquiryId, md5Pwd, paymentAmt, localDebitId, paymentTip, RETRY_TIMES);
        System.out.println(paymentResp);
    }


}
