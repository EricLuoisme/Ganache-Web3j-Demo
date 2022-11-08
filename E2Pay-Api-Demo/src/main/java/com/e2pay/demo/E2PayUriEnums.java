package com.e2pay.demo;

/**
 * @author Roylic
 * 2022/11/8
 */
public enum E2PayUriEnums {


    AUTHORIZATION("/rest/h2h/authorization/");


    public String uri;

    E2PayUriEnums(String uri) {
        this.uri = uri;
    }
}
