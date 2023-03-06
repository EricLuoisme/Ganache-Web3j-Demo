package com.example.web3j.combination.utils;

import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Originally from OnBoarding-eip712
 *
 * @author Roylic
 * 2023/3/6
 */
public class SignatureService {

    private final Credentials credentials;

    public SignatureService() {
        String privateKey1 = "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3";
        credentials = Credentials.create(privateKey1);
        System.out.println("Address: " + credentials.getAddress());
    }

    public String sign(byte[] user, byte[] proof, String useraddress) throws Exception {
        System.out.println(String.format("params: %s,%s,000000000000000000000000%s",
                Numeric.toHexString(user), Numeric.toHexString(proof), useraddress.substring(2)));

        String proofStr = createEIP712Proof(user, proof, useraddress);

        // sign
        Sign.SignatureData signature = Sign.signMessage(Numeric.hexStringToByteArray(proofStr), credentials.getEcKeyPair(), false);
        ByteBuffer sigBuffer = ByteBuffer.allocate(signature.getR().length + signature.getS().length + 1);
        sigBuffer.put(signature.getR());
        sigBuffer.put(signature.getS());
        sigBuffer.put(signature.getV());

        System.out.println(String.format("signed proof: %s", Numeric.toHexString(sigBuffer.array())));
        return Numeric.toHexStringNoPrefix(sigBuffer.array());
    }

    public String createEIP712Proof(byte[] user, byte[] proof, String useraddress) {
        byte[] domainSep = Hash.sha3("EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)".getBytes());
        byte[] akycType = Hash.sha3("AKYC(string user,string proof,address useraddress)".getBytes());
        String domainAsString = Numeric.toHexString(domainSep) +
                Numeric.toHexString(Hash.sha3("invite_me Demo".getBytes())).substring(2) +
                Numeric.toHexString(Hash.sha3("2".getBytes())).substring(2) +
                Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(BigInteger.valueOf(5777), 32)) +
                "0000000000000000000000001C56346CD2A2Bf3202F771f50d3D14a367B48070" +
                "f2d857f4a3edcb9b78b4d503bfe733db1e3f6cdc2b7971ee739626c97e86a558";
        String proofStr = Numeric.toHexStringNoPrefix(Hash.sha3(
                Numeric.hexStringToByteArray(
                        "0x1901" +
                                Numeric.toHexStringNoPrefix(Hash.sha3(Numeric.hexStringToByteArray(domainAsString))) +
                                Numeric.toHexStringNoPrefix(Hash.sha3(Numeric.hexStringToByteArray(
                                        Numeric.toHexStringNoPrefix(akycType) +
                                                Numeric.toHexStringNoPrefix(user) +
                                                Numeric.toHexStringNoPrefix(proof) +
                                                "000000000000000000000000" + useraddress.substring(2).toLowerCase()))
                                ))));
        System.out.println("proof plain:" + proofStr);
        return proofStr;
    }

    public static void main(String[] args) {
        SignatureService signatureService = new SignatureService();
    }

}
