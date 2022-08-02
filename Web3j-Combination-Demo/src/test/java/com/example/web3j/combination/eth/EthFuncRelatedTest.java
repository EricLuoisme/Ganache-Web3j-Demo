package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
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
        String decimalFunc_Sig = FunctionEncoder.encode(EthContractCallingTest.decimalFunc);
        System.out.println(cutSignature(decimalFunc_Sig));

        // 0xddf252ad
        String transferEvent_Sig = FunctionEncoder.encode(new Function(
                "Transfer",
                Arrays.asList(Address.DEFAULT, Address.DEFAULT, Uint256.DEFAULT),
                Collections.emptyList()
        ));
        System.out.println(cutSignature(transferEvent_Sig));

        // 0xc5cb9b51
        String transferCrossChainFunc_Sig = FunctionEncoder.encode(new Function(
                "transferCrossChain",
                Arrays.asList(Utf8String.DEFAULT, Uint256.DEFAULT, Uint256.DEFAULT, Bytes32.DEFAULT),
                Collections.singletonList(TypeReference.create(Bool.class))));
        System.out.println(cutSignature(transferCrossChainFunc_Sig));

        // 0x282dd181
        String transferCrossChainEvent_Sig = FunctionEncoder.encode(new Function(
                "TransferCrossChain",
                Arrays.asList(Address.DEFAULT, Utf8String.DEFAULT, Uint256.DEFAULT, Uint256.DEFAULT, Bytes32.DEFAULT),
                Collections.emptyList()
        ));
        System.out.println(cutSignature(transferCrossChainEvent_Sig));


    }


    private static String cutSignature(String wholeSignature) {
        return wholeSignature.length() > 10 ? wholeSignature.substring(0, 10) : wholeSignature;
    }


}
