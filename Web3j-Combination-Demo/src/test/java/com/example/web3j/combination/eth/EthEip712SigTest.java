package com.example.web3j.combination.eth;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.crypto.StructuredDataEncoder;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {
 * "types": {
 * "EIP712Domain": [
 * {
 * "name": "name",
 * "type": "string"
 * },
 * {
 * "name": "version",
 * "type": "string"
 * },
 * {
 * "name": "chainId",
 * "type": "uint256"
 * },
 * {
 * "name": "verifyingContract",
 * "type": "address"
 * }
 * ],
 * "Permit": [
 * {
 * "name": "owner",
 * "type": "address"
 * },
 * {
 * "name": "spender",
 * "type": "address"
 * },
 * {
 * "name": "value",
 * "type": "uint256"
 * },
 * {
 * "name": "nonce",
 * "type": "uint256"
 * },
 * {
 * "name": "deadline",
 * "type": "uint256"
 * }
 * ]
 * },
 * "domain": {
 * "name": "Cherry LPs",
 * "version": "1",
 * "chainId": 66,
 * "verifyingContract": "0x919cEE4766eD56CA3FA5028B16BF22D43a7A5d43"
 * },
 * "primaryType": "Permit",
 * "message": {
 * "owner": "0xd41ef6af97cb0f54eab5c1ebfe62478f40a78259",
 * "spender": "0x865bfde337C8aFBffF144Ff4C29f9404EBb22b15",
 * "value": "1000000000000000000",
 * "nonce": "0x02",
 * "deadline": 1629271118
 * }
 * }
 *
 * @author Roylic
 * 2023/3/6
 */
public class EthEip712SigTest {

    private static final String JSON = "{\"types\":{ \"EIP712Domain\":[ { \"name\":\"name\", \"type\":\"string\" }, { \"name\":\"version\", \"type\":\"string\" }, { \"name\":\"chainId\", \"type\":\"uint256\" }, { \"name\":\"verifyingContract\", \"type\":\"address\" } ], \"Permit\":[ { \"name\":\"owner\", \"type\":\"address\" }, { \"name\":\"spender\", \"type\":\"address\" }, { \"name\":\"value\", \"type\":\"uint256\" }, { \"name\":\"nonce\", \"type\":\"uint256\" }, { \"name\":\"deadline\", \"type\":\"uint256\" } ] }, \"domain\":{ \"name\":\"Cherry LPs\", \"version\":\"1\", \"chainId\":66, \"verifyingContract\":\"0x919cEE4766eD56CA3FA5028B16BF22D43a7A5d43\" }, \"primaryType\":\"Permit\", \"message\":{ \"owner\":\"0xd41ef6af97cb0f54eab5c1ebfe62478f40a78259\", \"spender\":\"0x865bfde337C8aFBffF144Ff4C29f9404EBb22b15\", \"value\":\"1000000000000000000\", \"nonce\":\"0x02\", \"deadline\":1629271118 }}";


    private static final String pKey = "";

    @Test
    public void hashCalculating() throws IOException {
        ECKeyPair signAcc = Credentials.create(pKey).getEcKeyPair();
        StructuredDataEncoder structuredDataEncoder = new StructuredDataEncoder(JSON);
        Sign.SignatureData signatureData = Sign.signMessage(structuredDataEncoder.hashStructuredData(), signAcc, false);

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Uint8(Numeric.toBigInt(signatureData.getV())));
        inputParameters.add(new Bytes32((signatureData.getR())));
        inputParameters.add(new Bytes32((signatureData.getS())));

        String data = FunctionEncoder.encodeConstructor(inputParameters);
        System.out.println(data);
    }

    @Test
    public void comprehensionTest() throws Exception {
        // 1. construct Domain-Data first


    }

    /**
     * Get DomainType
     * @return
     */
    private static JSONArray getDomainType() {
        JSONArray array = new JSONArray();

        JSONObject name = new JSONObject();
        name.put("name", "name");
        name.put("type", "string");

        JSONObject version = new JSONObject();
        version.put("name", "version");
        version.put("type", "string");

        JSONObject chainId = new JSONObject();
        chainId.put("name", "chainId");
        chainId.put("type", "uint256");

        JSONObject verifyingContract = new JSONObject();
        verifyingContract.put("name", "verifyingContract");
        verifyingContract.put("type", "addres");

        array.add(name);
        array.add(version);
        array.add(chainId);
        array.add(verifyingContract);

        return array;
    }


}

