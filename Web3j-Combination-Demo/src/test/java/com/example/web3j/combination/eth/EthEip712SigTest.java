package com.example.web3j.combination.eth;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.StructuredDataEncoder;
import org.web3j.utils.Numeric;

import java.io.IOException;

/**
 * @author Roylic
 * 2023/3/6
 */
public class EthEip712SigTest {

    private static final String JSON = "{\"types\":{\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"},{\"name\":\"chainId\",\"type\":\"uint256\"},{\"name\":\"verifyingContract\",\"type\":\"address\"}],\"Permit\":[{\"name\":\"owner\",\"type\":\"address\"},{\"name\":\"spender\",\"type\":\"address\"},{\"name\":\"value\",\"type\":\"uint256\"},{\"name\":\"nonce\",\"type\":\"uint256\"},{\"name\":\"deadline\",\"type\":\"uint256\"}]},\"domain\":{\"name\":\"Cherry LPs\",\"version\":\"1\",\"chainId\":66,\"verifyingContract\":\"0x919cEE4766eD56CA3FA5028B16BF22D43a7A5d43\"},\"primaryType\":\"Permit\",\"message\":{\"owner\":\"0xd41ef6af97cb0f54eab5c1ebfe62478f40a78259\",\"spender\":\"0x865bfde337C8aFBffF144Ff4C29f9404EBb22b15\",\"value\":\"1000000000000000000\",\"nonce\":\"0x02\",\"deadline\":1629271118}}";


    private static final String pKey = "";

    @Test
    public void hashCalculating() throws IOException {
        StructuredDataEncoder structuredDataEncoder = new StructuredDataEncoder(JSON);
        System.out.println("txHash of eip721 msgHash: " + Numeric.toHexString(structuredDataEncoder.hashStructuredData()));

//        ECKeyPair signAcc = Credentials.create(pKey).getEcKeyPair();
//        Sign.SignatureData signatureData = Sign.signMessage(structuredDataEncoder.hashStructuredData(), signAcc, false);
//
//        List<Type> inputParameters = new ArrayList<>();
//        inputParameters.add(new Uint8(Numeric.toBigInt(signatureData.getV())));
//        inputParameters.add(new Bytes32((signatureData.getR())));
//        inputParameters.add(new Bytes32((signatureData.getS())));
//
//        String data = FunctionEncoder.encodeConstructor(inputParameters);
//        System.out.println(data);
    }

    @Test
    public void comprehensionTest() throws Exception {

        // construct eip-712 structured data
        JSONObject wholeStructuredData = new JSONObject();

        // 1. domain-data-type
        JSONObject types = new JSONObject();
        JSONArray domainType = getDomainType();
        types.put("EIP712Domain", domainType);
        // 2. abi-params-type
        String primaryTypeName = "Permit";
        JSONArray abiType = getAbiType();
        types.put(primaryTypeName, abiType);
        wholeStructuredData.put("types", types);

        // 3. domain-data
        JSONObject domainData = getDomainData("Cherry LPs", "1", 66L, "0x919cEE4766eD56CA3FA5028B16BF22D43a7A5d43");
        wholeStructuredData.put("domain", domainData);
        // 4. primaryType
        wholeStructuredData.put("primaryType", primaryTypeName);
        // 5. abi-params-data
        JSONObject abiData = getAbiData("0xd41ef6af97cb0f54eab5c1ebfe62478f40a78259",
                "0x865bfde337C8aFBffF144Ff4C29f9404EBb22b15",
                "1000000000000000000", "0x02", 1629271118L);
        wholeStructuredData.put("message", abiData);

        // parsing and sign
        StructuredDataEncoder structuredDataEncoder = new StructuredDataEncoder(wholeStructuredData.toJSONString());
        StructuredDataEncoder structuredDataEncoder2 = new StructuredDataEncoder(JSON);

        System.out.println("txHash of eip721 msgHash: " + Numeric.toHexString(structuredDataEncoder.hashStructuredData()));
        System.out.println("txHash of eip721 json msgHash: " + Numeric.toHexString(structuredDataEncoder2.hashStructuredData()));
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
    private static JSONObject getDomainData(String name, String version, Long chainId, String verifyingContract) {
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
        param_1.put("name", "owner");
        param_1.put("type", "address");

        JSONObject param_2 = new JSONObject();
        param_2.put("name", "spender");
        param_2.put("type", "address");

        JSONObject param_3 = new JSONObject();
        param_3.put("name", "value");
        param_3.put("type", "uint256");

        JSONObject param_4 = new JSONObject();
        param_4.put("name", "nonce");
        param_4.put("type", "uint256");

        JSONObject param_5 = new JSONObject();
        param_5.put("name", "deadline");
        param_5.put("type", "uint256");

        array.add(param_1);
        array.add(param_2);
        array.add(param_3);
        array.add(param_4);
        array.add(param_5);
        return array;
    }

    /**
     * return Abi data
     */
    private static JSONObject getAbiData(String owner, String spender, String value, String nonce, Long deadline) {
        JSONObject msg = new JSONObject();
        msg.put("owner", owner);
        msg.put("spender", spender);
        msg.put("value", value);
        msg.put("nonce", nonce);
        msg.put("deadline", deadline);
        return msg;
    }


}

