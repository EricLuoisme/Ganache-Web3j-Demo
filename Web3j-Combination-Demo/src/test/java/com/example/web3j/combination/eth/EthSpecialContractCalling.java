package com.example.web3j.combination.eth;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * @author Roylic
 * 2023/3/3
 */
public class EthSpecialContractCalling {

    private static final String web3Url = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    public static final String contractAdd = "0xa9E628B29169ef448dBf362ec068EC1F414505BC";

    public static final String supportTokenAddress = "0xBA62BCfcAaFc6622853cca2BE6Ac7d845BC0f2Dc";

    public static final String marketMakerAddress = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";

    public static final String PRI_KEY = "";


    @Test
    public void marketMakerValidationTest() throws IOException {
        // encode
        Function marketMakerFunc = new Function(
                "marketMaker",
                Collections.singletonList(new Address(marketMakerAddress)),
                Collections.singletonList(TypeReference.create(Bool.class)));
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

    @Test
    public void marketMakerRegistrationTest() throws IOException {
        // encode input data
        Function registerMarketMaker = new Function(
                "registerMarketMaker",
                Collections.singletonList(new Address(marketMakerAddress)),
                Collections.singletonList(TypeReference.create(Bool.class)));
        String data = FunctionEncoder.encode(registerMarketMaker);
        // construct txn
        constructAndCallingContractFunction(data, PRI_KEY);
    }

    @Test
    public void marketMakerTokenAddTest() throws IOException {
        // encode input data
        Function marketMakerAddTokenFunc = new Function(
                "marketMakerAddToken",
                Collections.singletonList(new Address(supportTokenAddress)),
                Collections.singletonList(TypeReference.create(Bool.class)));
        String data = FunctionEncoder.encode(marketMakerAddTokenFunc);// construct txn
        constructAndCallingContractFunction(data, PRI_KEY);
    }


    @Test
    public void supportTokenCheckingTest() throws IOException {
        /// encode
        Function marketMakerTokenFunc = new Function(
                "getMarketMakerToken",
                Collections.singletonList(new Address(marketMakerAddress)),
                Collections.singletonList(new TypeReference<DynamicArray<PaymentToken>>() {
                }));

        String encode = FunctionEncoder.encode(marketMakerTokenFunc);
        System.out.println("Market Maker Func coding: " + encode);

        // call contract
        Transaction mmCallingTxn = Transaction.createEthCallTransaction(contractAdd, contractAdd, encode);
        EthCall send = web3j.ethCall(mmCallingTxn, DefaultBlockParameterName.LATEST).send();

        // decode
        List<Type> decode = FunctionReturnDecoder.decode(send.getValue(), marketMakerTokenFunc.getOutputParameters());
        System.out.println();
    }


    /**
     * Construct txn inputs & execute
     */
    private void constructAndCallingContractFunction(String data, String priKey) throws IOException {
        Credentials credentials = Credentials.create(priKey);
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        // another stuff need to be filled
        long chainId = 5; // for Goerli
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
        BigInteger maxFeePerGas = BigInteger.valueOf(50_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(100_000L);
        // for interact with contract, value have to input 0
        BigInteger value = BigInteger.valueOf(0L);
        RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, contractAdd, value, data, maxPriorityFeePerGas, maxFeePerGas);
        byte[] signedMsg = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMsg);

        String txHash = Hash.sha3(hexValue);
        System.out.println("OffChain txHash: " + txHash);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        System.out.println("OnChain txHash: " + ethSendTransaction.getTransactionHash());
    }

    @Data
    public class PaymentToken extends DynamicStruct {
        private Address address;
        private Utf8String name;
        private Utf8String symbol;
        private Uint8 decimals;

        public PaymentToken(Address address, Utf8String name, Utf8String symbol, Uint8 decimals) {
            super(address, name, symbol, decimals);
            this.address = address;
            this.name = name;
            this.symbol = symbol;
            this.decimals = decimals;
        }
    }

}
