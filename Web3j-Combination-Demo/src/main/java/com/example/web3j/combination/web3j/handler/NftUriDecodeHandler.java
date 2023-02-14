package com.example.web3j.combination.web3j.handler;

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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * For better re-used
 *
 * @author Roylic
 * 2023/2/14
 */
public class NftUriDecodeHandler {

    public static String tokenBaseUriRetrieving(Web3j web3j, String contract, List<String> tokenIdList, SupportErc supportErc) {

        String tokenBaseUri = "";
        try {
            switch (supportErc) {
                case ERC_721:
                    return contractTokenUri(web3j, contract, tokenIdList.get(0), "tokenURI");
                case ERC_1155_SINGLE:
                case ERC_1155_BATCH:
                    return contractTokenUri(web3j, contract, tokenIdList.get(0), "uri");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tokenBaseUri;
    }

    private static String contractTokenUri(Web3j web3j, String contract, String tokenIdStr, String funcName) throws IOException {

        BigInteger tokenId = new BigInteger(tokenIdStr);

        // construct func + params
        Uint256 tokenId256 = new Uint256(tokenId);
        Function tokenURIFunc = new Function(
                funcName,
                Collections.singletonList(tokenId256),
                Collections.singletonList(TypeReference.create(Utf8String.class)));

        String encode = FunctionEncoder.encode(tokenURIFunc);
        System.out.println("Code >>> " + encode.substring(0, 10));

        // call contract for url
        Transaction reqTxn = Transaction.createEthCallTransaction(contract, contract, encode);
        EthCall callResult = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send();
        List<Type> tokenUriDecoded = FunctionReturnDecoder.decode(callResult.getValue(), tokenURIFunc.getOutputParameters());

        // base url retrieve from contract
        Utf8String tokenBaseUrl = (Utf8String) tokenUriDecoded.get(0);
        String baseUrl = tokenBaseUrl.getValue();
        System.out.println("Token base uri >>> " + baseUrl);
        return baseUrl;
    }

    public enum SupportErc {
        ERC_721,
        ERC_1155_SINGLE,
        ERC_1155_BATCH
    }
}
