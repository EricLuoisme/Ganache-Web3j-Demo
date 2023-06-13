package com.example.web3j.combination.evm.eth;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
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
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Roylic
 * 2023/3/3
 */
public class SepoliaSpecContractInteraction {

    private static final String web3Url = "https://eth-sepolia.g.alchemy.com/v2/QLMIscpEP9Ok7kjHjnW2F65XN11crn55";
    private static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    private static final String contractAddress = "0x1bBB032517033C866Afd83D37234d3F6E8d4Fcc2";
    private static final String supportTokenAddress = "0x00e4523e0De972ffC7268470aa114e47d14241a9";
    private static final String marketMakerAddress = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";


    @Test
    public void getHeightAndChainId() throws IOException {
        EthBlockNumber resp = web3j.ethBlockNumber().send();
        System.out.println(resp.getBlockNumber());

        EthChainId respChain = web3j.ethChainId().send();
        System.out.println(respChain.getChainId());
    }

    @Test
    public void marketMakerRegistrationTest() throws IOException {
        // encode input data
        Function registerMarketMaker = new Function(
                "registerMarketMaker",
                Collections.singletonList(new Address(marketMakerAddress)),
                Collections.singletonList(TypeReference.create(Bool.class)));
        String data = FunctionEncoder.encode(registerMarketMaker);
        // construct txn, 0xb7f45cbd2e48ca2a54cc5f0e8e80fefaf3e5e0cdddfdbb9bb69faf56c475325f
        constructAndCallingContractFunction(data, contractAddress, "");
    }

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
        Transaction mmCallingTxn = Transaction.createEthCallTransaction(contractAddress, contractAddress, encode);
        EthCall send = web3j.ethCall(mmCallingTxn, DefaultBlockParameterName.LATEST).send();

        // decoding
        List<Type> decode = FunctionReturnDecoder.decode(send.getValue(), marketMakerFunc.getOutputParameters());
        Bool returnVal = (Bool) decode.get(0);
        System.out.println(returnVal.getValue());
    }

    @Test
    public void marketMakerTokenAddTest() throws IOException {
        // encode input data
        Function marketMakerAddTokenFunc = new Function(
                "marketMakerAddToken",
                Collections.singletonList(new Address("0x36F0A040C8e60974d1F34b316B3e956f509Db7e5")),
                Collections.singletonList(TypeReference.create(Bool.class)));
        String data = FunctionEncoder.encode(marketMakerAddTokenFunc);
        // construct txn, 0xc933312280dad59dfa62d2d54ac52c1ae37ac4326b106032750a9cb514e87845
        constructAndCallingContractFunction(data, contractAddress, "");
    }

    @Test
    public void marketMakerTokenDelTest() throws IOException {
        // encode input data
        Function marketMakerAddTokenFunc = new Function(
                "marketMakerDelToke",
                Collections.singletonList(new Address("0xBA62BCfcAaFc6622853cca2BE6Ac7d845BC0f2Dc")),
                Collections.singletonList(TypeReference.create(Bool.class)));
        String data = FunctionEncoder.encode(marketMakerAddTokenFunc);
        // construct txn, 0x5a94ecb29d1128f557bb50ec457730026f9eef7459e083ba4a88fd99e7c21a5d
        constructAndCallingContractFunction(data, contractAddress, "");
    }

    @Test
    public void supportTokenCheckingTest() throws IOException {
        /// encode
        Function marketMakerTokenFunc = new Function(
                "getMarketMakerToken",
                Collections.singletonList(new Address(marketMakerAddress)),
                Collections.singletonList(new TypeReference<DynamicArray<EthSpecialContractCalling.PaymentToken>>() {
                })
        );

        String encode = FunctionEncoder.encode(marketMakerTokenFunc);
        System.out.println("Market Maker Func coding: " + encode);

        // call contract
        Transaction mmCallingTxn = Transaction.createEthCallTransaction(contractAddress, contractAddress, encode);
        EthCall send = web3j.ethCall(mmCallingTxn, DefaultBlockParameterName.LATEST).send();

        // decode
        List<Type> decode = FunctionReturnDecoder.decode(send.getValue(), marketMakerTokenFunc.getOutputParameters());
        DynamicArray<EthSpecialContractCalling.PaymentToken> paymentTokenDynamicArray = (DynamicArray<EthSpecialContractCalling.PaymentToken>) decode.get(0);
        List<EthSpecialContractCalling.PaymentToken> paymentTokenList = paymentTokenDynamicArray.getValue();
        paymentTokenList.forEach(paymentToken -> {
            System.out.println();
            System.out.println("Token Address: " + paymentToken.getAddress());
            System.out.println("Token name: " + paymentToken.getName());
            System.out.println("Token symbol: " + paymentToken.getSymbol());
            System.out.println("Token decimals: " + paymentToken.getDecimals());
        });
    }

    @Test
    public void approveToken() throws Exception {

        BigInteger maximum = new BigInteger("2").pow(256).subtract(BigInteger.ONE);
        Function approveFunc = new Function(
                "approve",
                Arrays.asList(new Address(contractAddress), new Uint256(maximum)),
                Collections.singletonList(TypeReference.create(Bool.class))
        );
        String data = FunctionEncoder.encode(approveFunc);
        System.out.println("Approve Func input data coding: " + data);

        // call contract, 0x1e4f5a857b54e1dd609fc7ecd5cf261937a25c07edf7d85c40a70a02e006e1f5
        constructAndCallingContractFunction(data, supportTokenAddress, "");
    }

    @Test
    public void callingPaymentByUserSimulation() throws IOException {


        String inputJsonStr = "{\"data\":{\"requestId\":\"20230302102300\",\"merchantNo\":\"202201Maker_2ai75\",\"orderNo\":\"20230314O_CR01167876390609434469\",\"merchantOrderNo\":\"12342fjoi1u98r_1678763906388\",\"chainId\":11155111,\"baseContract\":\"0x00e4523e0De972ffC7268470aa114e47d14241a9\",\"quoteContract\":\"0x07865c6E87B9F70255377e024ace6630C1Eaa37F\",\"baseAmt\":\"1379259165450000000\",\"quoteAmt\":\"1230000000000000000\",\"c2cRate\":1.12134892,\"payAddress\":\"0x36F0A040C8e60974d1F34b316B3e956f509Db7e5\",\"merchantAddress\":\"0x36F0A040C8e60974d1F34b316B3e956f509Db7e5\",\"deadline\":1678850306389,\"signR\":\"0x8654f2eecf52a18722be6839ca2b7d8fce332bdcd811aa4d9a8aacd469cf68dc\",\"signS\":\"0x50e2193f118cc7aa3c886bcd64e03d39341ad8e8e966a33903c2ef408d54633b\",\"signV\":\"0x1b\",\"accepted\":true},\"error\":null}";
        JSONObject jsonObject = JSONObject.parseObject(inputJsonStr);
        JSONObject dataObj = jsonObject.getJSONObject("data");

        String orderId = dataObj.getString("orderNo");
        String merchantOrderId = dataObj.getString("merchantOrderNo");
        String merchantAddress = dataObj.getString("merchantAddress");
        String baseCurrencyAddress = dataObj.getString("baseContract");
        String quoteCurrencyAddress = dataObj.getString("quoteContract");
        BigInteger baseCurrencyAmount = new BigInteger(dataObj.getString("baseAmt"));
        BigInteger quoteCurrencyAmount = new BigInteger(dataObj.getString("quoteAmt"));
        Long deadline = dataObj.getLong("deadline");
        BigInteger v = new BigInteger(1, Numeric.hexStringToByteArray(dataObj.getString("signV")));
        String hexR = dataObj.getString("signR");
        String hexS = dataObj.getString("signS");


        Function paymentByUser = new Function(
                "paymentByUser",
                Arrays.asList(
                        new Utf8String(orderId), // orderId
                        new Utf8String(merchantOrderId), // merchantOrderId
                        new Address(merchantAddress), // merchantAddress
                        new Address(baseCurrencyAddress), // baseCurrencyAddress
                        new Address(quoteCurrencyAddress), // quoteCurrencyAddress
                        new Uint256(baseCurrencyAmount), // baseCurrencyAmt
                        new Uint256(quoteCurrencyAmount), // quoteCurrencyAmt
                        new Uint256(deadline), // deadline
                        new Uint8(v), // v
                        new Bytes32(Numeric.hexStringToByteArray(hexR)), // r
                        new Bytes32(Numeric.hexStringToByteArray(hexS)) // s
                ),
                Collections.emptyList());
        String data = FunctionEncoder.encode(paymentByUser);
        System.out.println("paymentByUser Func input data coding: " + data);

        // call contract, 0x838f14169868080c21da1348580b9115111edc67607cac60417590ce556fa444
        constructAndCallingContractFunction(data, contractAddress, "");
    }

    @Test
    public void decodingPaymentByUserEvent() throws IOException {
        Event paymentByUserEvent = new Event("paymentByUserEvent",
                Arrays.asList(
                        TypeReference.create(Address.class, true), // fromAddress
                        TypeReference.create(Address.class, true), // toAddress
                        TypeReference.create(Utf8String.class), // orderId
                        TypeReference.create(Utf8String.class), // merchantOrderId
                        TypeReference.create(Address.class), // baseCurrencyAddress
                        TypeReference.create(Address.class), // quoteCurrencyAddress
                        TypeReference.create(Uint256.class), // baseCurrencyAmount
                        TypeReference.create(Uint256.class) // quoteCurrencyAmount
                ));
        String eventEncode = EventEncoder.encode(paymentByUserEvent);

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(8617192L)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(8617192L)),
                Collections.emptyList());
        filter.addOptionalTopics(eventEncode);

        EthLog log = web3j.ethGetLogs(filter).send();
        EthLog.LogObject logResult = (EthLog.LogObject) log.getLogs().get(0);
        List<String> topics = logResult.getTopics();

        // 1. decode indexed stuff over topics
        Type<Address> fromAddress = FunctionReturnDecoder.decodeIndexedValue(topics.get(1), TypeReference.create(Address.class));
        Type<Address> toAddress = FunctionReturnDecoder.decodeIndexedValue(topics.get(2), TypeReference.create(Address.class));

        // 2. decode non-indexed stuff over data
        List<Type> nonIndexedVal = FunctionReturnDecoder.decode(logResult.getData(), paymentByUserEvent.getNonIndexedParameters());
        Type<Utf8String> orderId = nonIndexedVal.get(0);
        Type<Utf8String> merchantOrderId = nonIndexedVal.get(1);
        Type<Address> baseCurrencyAddress = nonIndexedVal.get(2);
        Type<Address> quoteCurrencyAddress = nonIndexedVal.get(3);
        Type<Uint256> baseCurrencyAmount = nonIndexedVal.get(4);
        Type<Uint256> quoteCurrencyAmount = nonIndexedVal.get(5);

        System.out.println("Customer Address: " + fromAddress.getValue());
        System.out.println("Market Maker Address: " + toAddress.getValue());
        System.out.println("Platform Order Id: " + orderId.getValue());
        System.out.println("Market Maker Order Id: " + merchantOrderId.getValue());
        System.out.println("Customer Pay Token Contract: " + baseCurrencyAddress.getValue());
        System.out.println("Customer Rec Token Contract: " + quoteCurrencyAddress.getValue());
        System.out.println("Customer Pay Token Amount(Raw): " + baseCurrencyAmount.getValue());
        System.out.println("Customer Rec Token Amount(Raw): " + quoteCurrencyAmount.getValue());

    }


    /**
     * Get DomainType
     */
    private static JSONArray getDomainType() {
        JSONArray array = new JSONArray();

        JSONObject nameJson = new JSONObject();
        nameJson.put("name", "name");
        nameJson.put("type", "string");

        JSONObject versionJson = new JSONObject();
        versionJson.put("name", "version");
        versionJson.put("type", "string");

        JSONObject chainIdJson = new JSONObject();
        chainIdJson.put("name", "chainId");
        chainIdJson.put("type", "uint256");

        JSONObject verifyingContractJson = new JSONObject();
        verifyingContractJson.put("name", "verifyingContract");
        verifyingContractJson.put("type", "address");

        array.add(nameJson);
        array.add(versionJson);
        array.add(chainIdJson);
        array.add(verifyingContractJson);
        return array;
    }

    /**
     * Get DomainType
     */
    private static JSONObject getDomainData(String name, String version,
                                            Long chainId, String verifyingContract) {
        JSONObject domainObj = new JSONObject();
        domainObj.put("name", name);
        domainObj.put("version", version);
        domainObj.put("chainId", chainId);
        domainObj.put("verifyingContract", verifyingContract);
        return domainObj;
    }

    /**
     * return Abi Type
     */
    private static JSONArray getAbiType() {
        JSONArray array = new JSONArray();
        JSONObject param_1 = new JSONObject();
        param_1.put("name", "orderId");
        param_1.put("type", "bytes32");

        JSONObject param_2 = new JSONObject();
        param_2.put("name", "merchantOrderId");
        param_2.put("type", "bytes32");

        JSONObject param_3 = new JSONObject();
        param_3.put("name", "merchantAddress");
        param_3.put("type", "address");

        JSONObject param_4 = new JSONObject();
        param_4.put("name", "baseCurrencyAddress");
        param_4.put("type", "address");

        JSONObject param_5 = new JSONObject();
        param_5.put("name", "quoteCurrencyAddress");
        param_5.put("type", "address");

        JSONObject param_6 = new JSONObject();
        param_6.put("name", "baseCurrencyAmount");
        param_6.put("type", "uint256");

        JSONObject param_7 = new JSONObject();
        param_7.put("name", "quoteCurrencyAmount");
        param_7.put("type", "uint256");

        JSONObject param_8 = new JSONObject();
        param_8.put("name", "deadline");
        param_8.put("type", "uint256");

        array.add(param_1);
        array.add(param_2);
        array.add(param_3);
        array.add(param_4);
        array.add(param_5);
        array.add(param_6);
        array.add(param_7);
        array.add(param_8);
        return array;
    }

    /**
     * return Abi data, no matter we put String or Long inside, it would not change the txHash of the msg
     */
    private static JSONObject getAbiData(String orderId, String merchantOrderId, String merchantAddress,
                                         String baseCurrencyAddress, String quoteCurrencyAddress,
                                         String baseCurrencyAmount, String quoteCurrencyAmount,
                                         Long deadline) {
        JSONObject msg = new JSONObject();
        System.out.println("orderId: " + Hash.sha3String(orderId));
        System.out.println("merchantOrderId: " + Hash.sha3String(merchantOrderId));
        System.out.println("merchantAddress: " + merchantAddress);
        System.out.println("baseCurrencyAddress: " + baseCurrencyAddress);
        System.out.println("quoteCurrencyAddress: " + quoteCurrencyAddress);
        System.out.println("baseCurrencyAmount: " + baseCurrencyAmount);
        System.out.println("quoteCurrencyAmount: " + quoteCurrencyAmount);
        System.out.println("deadline: " + deadline);

        msg.put("orderId", Hash.sha3String(orderId));
        msg.put("merchantOrderId", Hash.sha3String(merchantOrderId));
        msg.put("merchantAddress", merchantAddress);
        msg.put("baseCurrencyAddress", baseCurrencyAddress);
        msg.put("quoteCurrencyAddress", quoteCurrencyAddress);
        msg.put("baseCurrencyAmount", baseCurrencyAmount);
        msg.put("quoteCurrencyAmount", quoteCurrencyAmount);
        msg.put("deadline", deadline);
        return msg;
    }


    /**
     * Construct txn inputs & execute
     */
    private void constructAndCallingContractFunction(String data, String callingContract, String priKey) throws IOException {
        Credentials credentials = Credentials.create(priKey);
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        // another stuff need to be filled
        long chainId = 11155111; // for Sepolia
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
        BigInteger maxFeePerGas = BigInteger.valueOf(50_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(1_000_000L);
        // for interact with contract, value have to input 0
        BigInteger value = BigInteger.valueOf(0L);
        RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, callingContract, value, data, maxPriorityFeePerGas, maxFeePerGas);
        byte[] signedMsg = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMsg);

        String txHash = Hash.sha3(hexValue);
        System.out.println("OffChain txHash: " + txHash);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        if (ethSendTransaction.hasError()) {
            System.out.println("Error received: " + ethSendTransaction.getError().getMessage());
        } else {
            System.out.println("OnChain txHash: " + ethSendTransaction.getTransactionHash());
        }
    }


}
