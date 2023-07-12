package com.example.web3j.combination.evm.fxEvm;

import com.example.web3j.combination.web3j.EthLogConstants;
import com.example.web3j.combination.web3j.handler.NftUriDecodeHandler;
import org.junit.jupiter.api.Test;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * chainId -> 90001
 *
 * @author Roylic
 * 2022/11/7
 */
public class FxEvmTest {

    //    private static final String web3Url = "https://fx-json-web3.functionx.io:8545";
    private static final String web3Url = "https://testnet-fx-json-web3.functionx.io:8545";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));


    @Test
    public void testConnection() {
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            EthChainId send = web3j.ethChainId().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println();
            System.out.println(clientVersion);
            System.out.println(send.getChainId());
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlock() throws IOException {
        EthBlock block_5528640 = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("5528640")), false).send();
        System.out.println(block_5528640.toString());
    }


    @Test
    public void get721Log() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(8679536)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(8679536)),
                Collections.singletonList("0x9E4df6f08ceEcfEF170FCbF036B97789d5320ec3"));

        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_20_721));
        EthLog logs = web3j.ethGetLogs(filter).send();

        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;

            // filter target txn
            if (!"0x67909fba927f5171ba7f85c8d7acd247290c5497aa7038a2d1b0622784e9c567".equals(curLogObj.getTransactionHash())) {
                return;
            }

            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. from address
            String from = new Address(iterator.next()).getValue();

            // 3. to address
            String to = new Address(iterator.next()).getValue();

            // 4. tokenId
            String tokenStr = iterator.next();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(tokenStr, Utils.convert(EthLogConstants.EthFuncOutput.UINT256.getFuncOutputParams()));
            BigDecimal tokenId = new BigDecimal((BigInteger) valueDecodeList.get(0).getValue());

            System.out.println("From address " + from);
            System.out.println("To address " + to);
            System.out.println("Token id " + tokenId);
        });

    }


    @Test
    public void FoxContractNftDecoding() {

        String contract = "0x9E4df6f08ceEcfEF170FCbF036B97789d5320ec3";
        String tokenIdStr = "105";

        String tokenBaseUri = NftUriDecodeHandler.tokenBaseUriRetrieving(web3j, contract, Collections.singletonList(tokenIdStr), NftUriDecodeHandler.SupportErc.ERC_721);
        System.out.println(tokenBaseUri);
    }

    @Test
    public void crossChainAbiEncoding() {

        // 0xc5cb9b51
        String transferCrossChain = FunctionEncoder.encode(new Function(
                "transferCrossChain",
                Arrays.asList(Utf8String.DEFAULT, Uint256.DEFAULT, Uint256.DEFAULT, Bytes32.DEFAULT),
                Collections.singletonList(TypeReference.create(Bool.class))));
        System.out.println("old contract's abi signature: " + cutSignature(transferCrossChain));

        // 0x160d7c73
        String crossChain = FunctionEncoder.encode(new Function(
                "crossChain",
                Arrays.asList(Address.DEFAULT, Utf8String.DEFAULT, Uint256.DEFAULT, Uint256.DEFAULT, Bytes32.DEFAULT, Utf8String.DEFAULT),
                Collections.singletonList(TypeReference.create(Bool.class))
        ));
        System.out.println("new contract's abi signature: " + cutSignature(crossChain));
    }

    @Test
    public void delegationRelatedSigs() {

        // 0x9ddb511a
        String delegate = FunctionEncoder.encode(new Function(
                "delegate",
                Collections.singletonList(Utf8String.DEFAULT),
                Arrays.asList(TypeReference.create(Uint256.class), TypeReference.create(Uint256.class))));
        System.out.println("delegate signature: " + cutSignature(delegate));

        // 0x8dfc8897
        String unDelegate = FunctionEncoder.encode(new Function(
                "undelegate",
                Arrays.asList(Utf8String.DEFAULT, Uint256.DEFAULT),
                Arrays.asList(TypeReference.create(Uint256.class), TypeReference.create(Uint256.class), TypeReference.create(Uint256.class))
        ));
        System.out.println("unDelegate signature: " + cutSignature(unDelegate));

        // 0x31fb67c2
        String withdraw = FunctionEncoder.encode(new Function(
                "withdraw",
                Collections.singletonList(Utf8String.DEFAULT),
                Collections.singletonList(TypeReference.create(Uint256.class))
        ));
        System.out.println("withdraw signature: " + cutSignature(withdraw));

        // 0x49da433e
        String approveShares = FunctionEncoder.encode(new Function(
                "approveShares",
                Arrays.asList(Utf8String.DEFAULT, Address.DEFAULT, Uint256.DEFAULT),
                Collections.singletonList(TypeReference.create(Bool.class))
        ));
        System.out.println("approveShares signature: " + cutSignature(approveShares));

        // 0x161298c1
        String transferShares = FunctionEncoder.encode(new Function(
                "transferShares",
                Arrays.asList(Utf8String.DEFAULT, Address.DEFAULT, Uint256.DEFAULT),
                Arrays.asList(TypeReference.create(Uint256.class), TypeReference.create(Uint256.class))
        ));
        System.out.println("transferShares signature: " + cutSignature(transferShares));

        // 0xdc6ffc7d
        String transferFromShares = FunctionEncoder.encode(new Function(
                "transferFromShares",
                Arrays.asList(Utf8String.DEFAULT, Address.DEFAULT, Address.DEFAULT, Uint256.DEFAULT),
                Arrays.asList(TypeReference.create(Uint256.class), TypeReference.create(Uint256.class))
        ));
        System.out.println("transferFromShares signature: " + cutSignature(transferFromShares));

    }




    private static String cutSignature(String wholeSignature) {
        return wholeSignature.length() > 10 ? wholeSignature.substring(0, 10) : wholeSignature;
    }
}
