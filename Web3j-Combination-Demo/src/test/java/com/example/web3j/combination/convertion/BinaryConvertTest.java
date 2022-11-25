package com.example.web3j.combination.convertion;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

/**
 * @author Roylic
 * 2022/11/25
 */
public class BinaryConvertTest {


    @Test
    public void convert2Binary() {
        String contract = "0x34df14d61C2F18907dd32425BF4FA71C111f7834";
        byte[] decode = Hex.decode(contract.startsWith("0x") ? contract.substring(2) : contract);
        System.out.println(decode);
    }




}
