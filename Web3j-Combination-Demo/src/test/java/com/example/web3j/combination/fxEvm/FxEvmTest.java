package com.example.web3j.combination.fxEvm;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * @author Roylic
 * 2022/11/7
 */
public class FxEvmTest {


    private static final String web3Url = "https://";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));


    @Test
    public void testConnection() {
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println();
            System.out.println(clientVersion);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlock() throws IOException {
        EthBlock block_5528640 = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("5528640")), false).send();
        System.out.println();
    }

    @Test
    public void FoxContractNftDecoding() throws IOException {
        String contract = "0x9E4df6f08ceEcfEF170FCbF036B97789d5320ec3";
        BigInteger tokenId = new BigInteger("105");

        // construct for func + params
        Uint256 tokenId256 = new Uint256(tokenId);
        Function tokenUriFunc = new Function(
                "tokenURI",
                Collections.singletonList(tokenId256),
                Collections.singletonList(TypeReference.create(Utf8String.class))
        );

        String encode = FunctionEncoder.encode(tokenUriFunc);
        System.out.println("ERC-721-Code >>> " + encode.substring(0, 10));

        // call contract for url
        Transaction reqTxn = Transaction.createEthCallTransaction(contract, contract, encode);
        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();
        List<Type> tokenUriDecoded = FunctionReturnDecoder.decode(callResult.getValue(), tokenUriFunc.getOutputParameters());

        // decode token url
        Utf8String tokenBaseUrl = (Utf8String) tokenUriDecoded.get(0);
        System.out.println("ERC-721 token base uri >>> " + tokenBaseUrl.getValue());

        // replace {id} with real id, for this OPEN_SEA's contract, we need to convert tokenId to hex
        System.out.println("Token Id in hex >>> " + Numeric.toHexStringNoPrefix(tokenId));
    }


}
