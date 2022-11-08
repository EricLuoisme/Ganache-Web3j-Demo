package com.e2pay.demo;

/**
 * @author Roylic
 * 2022/11/8
 */
public enum E2PayUriEnums {


    AUTH_CODE("/rest/h2h/authorization/", BodyMediaType.JSON),

    OAUTH_TOKEN("/rest/oauth/token", BodyMediaType.FORM),

    CUSTOMER_INFO("/b2b/customer/me/account", BodyMediaType.NONE),

    CUSTOMER_LIMIT("/b2b/customer/balance/limit", BodyMediaType.PATH_BODY),

    QRIS_INQUIRY("/b2b/customer/me/transaction/qris/inquiry", BodyMediaType.PATH_BODY),

    QRIS_PAYMENT("/b2b/customer/me/transaction/qris", BodyMediaType.JSON),


    ;


    public String uri;

    public BodyMediaType mediaType;

    E2PayUriEnums(String uri, BodyMediaType mediaType) {
        this.uri = uri;
        this.mediaType = mediaType;
    }


    public enum BodyMediaType {
        JSON, FORM, PATH_BODY, NONE
    }
}
