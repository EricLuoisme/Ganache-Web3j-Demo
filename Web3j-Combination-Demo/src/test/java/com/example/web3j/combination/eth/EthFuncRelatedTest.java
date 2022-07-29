package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.Arrays;
import java.util.Collections;

/**
 * Eth Function Related Test
 *
 * @author Roylic
 * 2022/7/28
 */
public class EthFuncRelatedTest {


    @Test
    public void getEthFunctionSigTest() {
        // 0x313ce567
        System.out.println(FunctionEncoder.encode(EthContractCallingTest.decimalFunc));

        // 0x282dd181
        System.out.println(FunctionEncoder.encode(new Function(
                "transferCrossChain",
                Arrays.asList((Type) TypeReference.create(Utf8String.class), (Type) TypeReference.create(Uint256.class), (Type) TypeReference.create(Uint256.class)),
                Collections.singletonList(TypeReference.create(Bool.class))
        )));


    }


}
