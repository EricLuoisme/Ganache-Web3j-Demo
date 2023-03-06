package com.example.web3j.combination.eth;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.web3j.combination.utils.OwnECDSASignUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
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
                })
        );

        String encode = FunctionEncoder.encode(marketMakerTokenFunc);
        System.out.println("Market Maker Func coding: " + encode);

        // call contract
        Transaction mmCallingTxn = Transaction.createEthCallTransaction(contractAdd, contractAdd, encode);
        EthCall send = web3j.ethCall(mmCallingTxn, DefaultBlockParameterName.LATEST).send();

        // decode
        List<Type> decode = FunctionReturnDecoder.decode(send.getValue(), marketMakerTokenFunc.getOutputParameters());
        DynamicArray<PaymentToken> paymentTokenDynamicArray = (DynamicArray<PaymentToken>) decode.get(0);
        List<PaymentToken> paymentTokenList = paymentTokenDynamicArray.getValue();
        paymentTokenList.forEach(paymentToken -> {
            System.out.println();
            System.out.println("Token Address: " + paymentToken.getAddress());
            System.out.println("Token name: " + paymentToken.getName());
            System.out.println("Token symbol: " + paymentToken.getSymbol());
            System.out.println("Token decimals: " + paymentToken.getDecimals());
        });
    }

    @Test
    public void paymentByUserTest() {

        String orderId = "20230303O_CR01167783311135710984";
        String merchantOrderId = "12342fjoi1u98rf31";
        String tokenAddress = "0x64544969ed7EBf5f083679233325356EbE738930";
        String merchantAddress = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";

        byte[] tradingPair = new byte[32];
        byte[] exchangeRate = new byte[32];
        byte[] inputBytes = Numeric.hexStringToByteArray("0x24");
        System.arraycopy(inputBytes, 0, tradingPair, 0, inputBytes.length);
        System.arraycopy(inputBytes, 0, exchangeRate, 0, inputBytes.length);

        long deadline = 1687919511947L;
        long amount = 100L;

        ValidateMarketMaker plainTxt = ValidateMarketMaker.builder()
                .tokenAddress(new Address(tokenAddress))
                .merchantAddress(new Address(merchantAddress))
                .tradingPair(new Bytes32(tradingPair))
                .exchangeRate(new Bytes32(exchangeRate))
                .deadline(new Uint256(deadline))
                .deadline(new Uint256(amount))
                .build();

        long[] rsv = OwnECDSASignUtil.signGetByteArr(
                JSONObject.toJSONString(plainTxt, SerializerFeature.SortField),
                Credentials.create(PRI_KEY).getEcKeyPair());

        System.out.println();

//        new Function(
//                "paymentByUser",
//                Arrays.asList(
//                        new Utf8String(orderId),
//                        new Utf8String(merchantOrderId),
//                        plainTxt.getTokenAddress(),
//                        plainTxt.getMerchantAddress(),
//                        plainTxt.getTradingPair(),
//                        plainTxt.getExchangeRate(),
//                        plainTxt.getDeadline(),
//                        plainTxt.getAmount(),
//                        new Uint256(rsvBytes[0]),
//                        new
//                ),
//                Collections.emptyList()
//        );

    }

    @Test
    public void paymentEventDecodingTest() {

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
    public static class PaymentToken extends DynamicStruct {

        private String address;
        private String name;
        private String symbol;
        private Long decimals;

        public PaymentToken(String address, String name, String symbol, Long decimals) {
            super(new Address(address), new Utf8String(name), new Utf8String(symbol), new Uint8(decimals));
            this.address = address;
            this.name = name;
            this.symbol = symbol;
            this.decimals = decimals;
        }

        public PaymentToken(Address address, Utf8String name, Utf8String symbol, Uint8 decimals) {
            super(address, name, symbol, decimals);
            this.address = address.getValue();
            this.name = name.getValue();
            this.symbol = symbol.getValue();
            this.decimals = decimals.getValue().longValue();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateMarketMaker {
        private Address tokenAddress;
        private Address merchantAddress;
        private Bytes32 tradingPair;
        private Bytes32 exchangeRate;
        private Uint256 deadline;
        private Uint256 amount;
    }

}
