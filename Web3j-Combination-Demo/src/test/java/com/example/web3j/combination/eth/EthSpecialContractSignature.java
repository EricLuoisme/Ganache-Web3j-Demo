package com.example.web3j.combination.eth;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Roylic
 * 2023/3/3
 */
public class EthSpecialContractSignature {

    private static final String web3Url = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    public static final String contractAddress = "0xa9E628B29169ef448dBf362ec068EC1F414505BC";

    public static final String supportTokenAddress = "0xBA62BCfcAaFc6622853cca2BE6Ac7d845BC0f2Dc";

    public static final String marketMakerAddress = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";

    public static final String tradingPair = "0x0000000000000000000000000000000000000000000000000000000000000005";
    public static final String exchangeRate = "0x0000000000000000000000000000000000000000000000000000000000000006";
    public static final Long deadline = 1978244195003L;
    public static final Long amt = 1000000000000000000L;


    @Test
    public void getChainId() throws IOException {
        EthChainId send = web3j.ethChainId().send();
        System.out.println(send.getChainId());
    }


    @Test
    public void comprehensionTest() throws Exception {

        // construct eip-712 structured data
        com.alibaba.fastjson2.JSONObject wholeStructuredData = new com.alibaba.fastjson2.JSONObject();

        // 1. domain-data-type
        com.alibaba.fastjson2.JSONObject types = new com.alibaba.fastjson2.JSONObject();
        types.put("EIP712Domain", getDomainType());
        // 2. abi-params-type
        String primaryTypeName = "ValidateMarketMakers";
        types.put(primaryTypeName, getAbiType());
        wholeStructuredData.put("types", types);

        // 3. domain-data
        wholeStructuredData.put("domain", getDomainData("FundCheck", "1", 5L, contractAddress));
        // 4. primaryType
        wholeStructuredData.put("primaryType", primaryTypeName);
        // 5. abi-params-data
        wholeStructuredData.put("message", getAbiData(supportTokenAddress, marketMakerAddress,
                tradingPair, exchangeRate, deadline, amt));

        // parsing and sign
        StructuredDataEncoder structuredDataEncoder = new StructuredDataEncoder(wholeStructuredData.toJSONString());
        String msgHash = Numeric.toHexStringNoPrefix(structuredDataEncoder.hashStructuredData());
        System.out.println("txHash of eip721 msgHash: " + msgHash);
    }

    @Test
    public void paymentByUserVRS() {
        String msgHash = "af388024982c964d0ec66e7202babb19aff3a0f797115f4564c35790679eb8f2";

        ECKeyPair signAcc = Credentials.create("").getEcKeyPair();
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(msgHash), signAcc, false);

        List<Type> inputParameters = new ArrayList<>();
        Uint8 v = new Uint8(Numeric.toBigInt(signatureData.getV()));
        Bytes32 r = new Bytes32((signatureData.getR()));
        Bytes32 s = new Bytes32((signatureData.getS()));
        System.out.println("v : " + v.getValue() + " in hex: " + Numeric.toHexStringWithPrefix(v.getValue()));
        System.out.println("r : " + Numeric.toHexString(r.getValue()));
        System.out.println("s : " + Numeric.toHexString(s.getValue()));

        inputParameters.add(v);
        inputParameters.add(r);
        inputParameters.add(s);

        String data = FunctionEncoder.encodeConstructor(inputParameters);
        System.out.println(data);
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

        // call contract
        constructAndCallingContractFunction(data, supportTokenAddress, "");
    }


    @Test
    public void callingPaymentByUser() throws IOException {
        Function paymentByUser = new Function(
                "paymentByUser",
                Arrays.asList(
                        new Utf8String("20230307O_CR01167817057936624383"), // orderId
                        new Utf8String("12342fjoi1u98r_1678170579727"), // merchantOrderId
                        new Address(supportTokenAddress), // tokenAddress
                        new Address(marketMakerAddress), // merchantAddress
                        new Bytes32(Numeric.hexStringToByteArray(tradingPair)), // tradingPair
                        new Bytes32(Numeric.hexStringToByteArray(exchangeRate)), // exchangeRate
                        new Uint256(deadline), // deadline
                        new Uint256(amt), // amount
                        new Uint8(27L), // v
                        new Bytes32(Numeric.hexStringToByteArray("0x2aeb13802db8735d0c66f2ae384ae38f2d5ac0ea2271be67e528874e01e9bd5d")), // r
                        new Bytes32(Numeric.hexStringToByteArray("0x68b365c7d92db42a69a43f3ed1c9da5b9330654cf2aeaa34dcd6891a8eeabae2")) // s
                ),
                Collections.emptyList());
        String data = FunctionEncoder.encode(paymentByUser);
        System.out.println("paymentByUser Func input data coding: " + data);

        // call contract
        constructAndCallingContractFunction(data, contractAddress, "");
    }


    /**
     * Get DomainType
     */
    private static JSONArray getDomainType() {
        JSONArray array = new JSONArray();

        com.alibaba.fastjson2.JSONObject nameJson = new com.alibaba.fastjson2.JSONObject();
        nameJson.put("name", "name");
        nameJson.put("type", "string");

        com.alibaba.fastjson2.JSONObject versionJson = new com.alibaba.fastjson2.JSONObject();
        versionJson.put("name", "version");
        versionJson.put("type", "string");

        com.alibaba.fastjson2.JSONObject chainIdJson = new com.alibaba.fastjson2.JSONObject();
        chainIdJson.put("name", "chainId");
        chainIdJson.put("type", "uint256");

        com.alibaba.fastjson2.JSONObject verifyingContractJson = new com.alibaba.fastjson2.JSONObject();
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
    private static com.alibaba.fastjson2.JSONObject getDomainData(String name, String version,
                                                                  Long chainId, String verifyingContract) {
        com.alibaba.fastjson2.JSONObject domainObj = new com.alibaba.fastjson2.JSONObject();
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
        com.alibaba.fastjson2.JSONObject param_1 = new com.alibaba.fastjson2.JSONObject();
        param_1.put("name", "tokenAddress");
        param_1.put("type", "address");

        com.alibaba.fastjson2.JSONObject param_2 = new com.alibaba.fastjson2.JSONObject();
        param_2.put("name", "merchantAddress");
        param_2.put("type", "address");

        com.alibaba.fastjson2.JSONObject param_3 = new com.alibaba.fastjson2.JSONObject();
        param_3.put("name", "tradingPair");
        param_3.put("type", "bytes32");

        com.alibaba.fastjson2.JSONObject param_4 = new com.alibaba.fastjson2.JSONObject();
        param_4.put("name", "exchangeRate");
        param_4.put("type", "bytes32");

        com.alibaba.fastjson2.JSONObject param_5 = new com.alibaba.fastjson2.JSONObject();
        param_5.put("name", "deadline");
        param_5.put("type", "uint256");

        com.alibaba.fastjson2.JSONObject param_6 = new com.alibaba.fastjson2.JSONObject();
        param_6.put("name", "amount");
        param_6.put("type", "uint256");

        array.add(param_1);
        array.add(param_2);
        array.add(param_3);
        array.add(param_4);
        array.add(param_5);
        array.add(param_6);
        return array;
    }

    /**
     * return Abi data
     */
    private static com.alibaba.fastjson2.JSONObject getAbiData(String tokenAddress, String merchantAddress,
                                                               String tradingPair, String exchangeRate, Long deadline,
                                                               Long amount) {
        com.alibaba.fastjson2.JSONObject msg = new JSONObject();
        msg.put("tokenAddress", tokenAddress);
        msg.put("merchantAddress", merchantAddress);
        msg.put("tradingPair", tradingPair);
        msg.put("exchangeRate", exchangeRate);
        msg.put("deadline", deadline);
        msg.put("amount", amount);
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
        long chainId = 5; // for Goerli
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
        BigInteger maxFeePerGas = BigInteger.valueOf(50_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(100_000L);
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
