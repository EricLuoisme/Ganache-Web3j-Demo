package com.e2pay.api.demo;

import com.e2pay.demo.E2PayReqUtils;
import org.junit.Test;

/**
 * @author Roylic
 * 2022/11/8
 */
public class E2PayUtilsCallingTest {


    @Test
    public void getCustomerCode() {
        String callingResult = E2PayReqUtils.getCustomerAuthorizationCode("082140988853", "E10ADC3949BA59ABBE56E057F20F883E", 1);
        System.out.println(callingResult);
    }

}
