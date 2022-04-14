package com.example.ganacheweb3jdemo;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Roylic
 * @date 2022/4/14
 */
public class EthContractCallingTest {

    public static final String INFURA_KOVAN_NODE_HTTP_LINK
            = "https://kovan.infura.io/v3/f1836cc85b4b4752adc841cc59eeb0c6";

    public static final Web3j web3j
            = Web3j.build(new HttpService(INFURA_KOVAN_NODE_HTTP_LINK));


    @Test
    public void callErc20Contract() throws IOException {
        EthLog.LogObject eth20LogObj = EthEventLogTest.getEth20LogObj();

        // ERC-20 获取该Token最小单位
        callGetDecimal(eth20LogObj);

        // ERC-20 获取Symbol (Unit)
        callGetSymbol(eth20LogObj);


    }

    private void callGetDecimal(EthLog.LogObject eth20LogObj) throws IOException {

        Function decimalFunc = new Function(
                "decimals",
                Collections.emptyList(),
                Collections.singletonList(TypeReference.create(Uint8.class)));

        String encodedDecimalFunc = FunctionEncoder.encode(decimalFunc);

        Transaction reqTxn = Transaction.createEthCallTransaction(
                eth20LogObj.getAddress(),
                eth20LogObj.getAddress(),
                encodedDecimalFunc);

        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();

        List<Type> decoded = FunctionReturnDecoder.decode(callResult.getValue(), decimalFunc.getOutputParameters());
        Uint8 decimals = (Uint8) decoded.get(0);
        System.out.println(decimals.getValue());
    }

    private void callGetSymbol(EthLog.LogObject eth20LogObj) throws IOException {

        Function symbolFunc = new Function(
                "symbol",
                Collections.emptyList(),
                Collections.singletonList(TypeReference.create(Utf8String.class))
        );
        String encodedSymbolFunc = FunctionEncoder.encode(symbolFunc);

        Transaction reqTxn_2 = Transaction.createEthCallTransaction(
                eth20LogObj.getAddress(),
                eth20LogObj.getAddress(),
                encodedSymbolFunc
        );

        EthCall callResult_2 = web3j.ethCall(reqTxn_2, DefaultBlockParameterName.LATEST).send();
        List<Type> decoded_2 = FunctionReturnDecoder.decode(callResult_2.getValue(), symbolFunc.getOutputParameters());
        Type type = decoded_2.get(0);
        System.out.println(type.toString());
    }
}
