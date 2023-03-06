package com.example.web3j.combination.eth;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
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
import java.util.List;

/**
 * @author Roylic
 * 2023/3/3
 */
public class EthSpecialContractSignatureV2 {

    private static final String web3Url = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    public static final String contractAddress = "0xa9E628B29169ef448dBf362ec068EC1F414505BC";

    public static final String supportTokenAddress = "0xBA62BCfcAaFc6622853cca2BE6Ac7d845BC0f2Dc";

    public static final String marketMakerAddress = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";

    public static final String PRI_KEY = "";


    @Test
    public void getChainId() throws IOException {
        EthChainId send = web3j.ethChainId().send();
        System.out.println(send.getChainId());
    }


    @Test
    public void comprehensionTest() throws Exception {

        // construct eip-712 structured data
        JSONObject wholeStructuredData = new JSONObject();

        // 1. domain-data-type
        JSONObject types = new JSONObject();
        types.put("EIP712Domain", getDomainType());
        // 2. abi-params-type
        String primaryTypeName = "ValidateMarketMakers";
        types.put(primaryTypeName, getAbiType());
        wholeStructuredData.put("types", types);

        // 3. domain-data
        wholeStructuredData.put("domain", getDomainData("FundCheck", "1",
                5L, "0xa9E628B29169ef448dBf362ec068EC1F414505BC"));
        // 4. primaryType
        wholeStructuredData.put("primaryType", primaryTypeName);
        // 5. abi-params-data
        wholeStructuredData.put("message", getAbiData(supportTokenAddress, marketMakerAddress,
                "0x0000000000000000000000000000000000000000000000000000000000000005",
                "0x0000000000000000000000000000000000000000000000000000000000000006",
                1999271118L, 10L));

        // parsing and sign
        StructuredDataEncoder structuredDataEncoder = new StructuredDataEncoder(wholeStructuredData.toJSONString());
        String msgHash = Numeric.toHexStringNoPrefix(structuredDataEncoder.hashStructuredData());
        System.out.println("txHash of eip721 msgHash: " + msgHash);

    }

    @Test
    public void paymentByUser() {
        String msgHash = "80dd7a261708a88ab2b0e6f6d7a30653e8aa745bd552794ed7c642746f4f7507";

        ECKeyPair signAcc = Credentials.create("").getEcKeyPair();
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(msgHash), signAcc, false);

        List<Type> inputParameters = new ArrayList<>();
        Uint8 v = new Uint8(Numeric.toBigInt(signatureData.getV()));
        Bytes32 r = new Bytes32((signatureData.getR()));
        Bytes32 s = new Bytes32((signatureData.getS()));
        System.out.println("v : " + Numeric.toHexStringWithPrefix(v.getValue()));
        System.out.println("r : " + Numeric.toHexString(r.getValue()));
        System.out.println("s : " + Numeric.toHexString(s.getValue()));

        inputParameters.add(v);
        inputParameters.add(r);
        inputParameters.add(s);

        String data = FunctionEncoder.encodeConstructor(inputParameters);
        System.out.println(data);
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
        param_1.put("type", "string");

        JSONObject param_2 = new JSONObject();
        param_2.put("name", "merchantOrderId");
        param_2.put("type", "string");

        JSONObject param_3 = new JSONObject();
        param_3.put("name", "leftTokenAddress");
        param_3.put("type", "address");

        JSONObject param_4 = new JSONObject();
        param_4.put("name", "tokenAddress");
        param_4.put("type", "address");

        JSONObject param_5 = new JSONObject();
        param_5.put("name", "merchantAddress");
        param_5.put("type", "address");

        JSONObject param_6 = new JSONObject();
        param_6.put("name", "leftAmount");
        param_6.put("type", "uint256");

        JSONObject param_7 = new JSONObject();
        param_7.put("name", "amount");
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
     * return Abi data
     */
    private static JSONObject getAbiData(String tokenAddress, String merchantAddress,
                                         String tradingPair, String exchangeRate, Long deadline,
                                         Long amount) {
        JSONObject msg = new JSONObject();
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
        RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, contractAddress, value, data, maxPriorityFeePerGas, maxFeePerGas);
        byte[] signedMsg = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMsg);

        String txHash = Hash.sha3(hexValue);
        System.out.println("OffChain txHash: " + txHash);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        System.out.println("OnChain txHash: " + ethSendTransaction.getTransactionHash());
    }


}
