package com.example.web3j.combination.evm.polygon;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * Polygon NFT Related
 *
 * @author Roylic
 * 2023/1/10
 */
public class PolygonNftTest {

    private static final String web3Url = "https://polygon-mainnet.nodereal.io/v1/";

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
    public void specificOpenSeaGifNft() throws IOException {
        String contract = "0x1777feb71b2eeeb5d75f2a29e12352afdc9da374";
        BigInteger tokenId = new BigInteger("308");

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


    @Test
    public void getOpenSeaNftTokenUrl() throws IOException {
        String contract = "0x2953399124F0cBB46d2CbACD8A89cF0599974963";
        BigInteger tokenId = new BigInteger("1867");

        // construct for func + params
        Uint256 tokenId256 = new Uint256(tokenId);
        Function tokenUriFunc = new Function(
                "uri",
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

        // insert
        System.out.println("Accessible token url >>> " + tokenBaseUrl.getValue().replace("{id}", Numeric.toHexStringNoPrefix(tokenId)));
    }

    @Test
    public void getLuckingDayNftTokenUrl() throws IOException {
        String contract = "0x50a289670273FFbD841beBc3a515DD968d65971A";
        BigInteger tokenId = new BigInteger("1867");

        // construct for func + params
        Uint256 tokenId256 = new Uint256(tokenId);
        Function tokenUriFunc = new Function(
                "tokenUri",
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

        // insert
        System.out.println("Accessible token url >>> " + tokenBaseUrl.getValue().replace("{id}", Numeric.toHexStringNoPrefix(tokenId)));
    }

}