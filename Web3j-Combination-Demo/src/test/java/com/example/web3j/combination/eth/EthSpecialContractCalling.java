package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Roylic
 * 2023/3/3
 */
public class EthSpecialContractCalling {

    private static final String web3Url = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    public static final String contractAdd = "0xa9E628B29169ef448dBf362ec068EC1F414505BC";

    @Test
    public void marketMakerRegistrationTest() throws IOException {
        // encode
        String pubAddress = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        Function marketMakerFunc = new Function(
                "marketMaker",
                Arrays.asList(new Address(pubAddress)),
                Arrays.asList(TypeReference.create(Bool.class)));
        String encode = FunctionEncoder.encode(marketMakerFunc);
        System.out.println("Market Maker Func coding: " + encode);

        // call contract
        Transaction mmCallingTxn = Transaction.createEthCallTransaction(contractAdd, contractAdd, encode);
        EthCall send = web3j.ethCall(mmCallingTxn, DefaultBlockParameterName.LATEST).send();

        // decoding
        List<Type> decode = FunctionReturnDecoder.decode(send.getValue(), marketMakerFunc.getOutputParameters());
        Bool returnVal = (Bool) decode.get(0);
        System.out.println(returnVal.getValue());
    }
}
