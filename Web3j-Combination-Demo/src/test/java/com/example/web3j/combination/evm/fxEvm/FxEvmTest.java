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


    @Test
    public void crossChainLogDecoding() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(9303183)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(9303183)),
                Collections.emptyList());

        Event crossChainEvt = new Event("CrossChain", Arrays.asList(
                TypeReference.create(Address.class, true), // sender
                TypeReference.create(Address.class, true), // token
                TypeReference.create(Utf8String.class), // denom
                TypeReference.create(Utf8String.class), // receipt
                TypeReference.create(Uint256.class), // amount
                TypeReference.create(Uint256.class), // fee
                TypeReference.create(Bytes32.class), // target
                TypeReference.create(Utf8String.class) // memo
        ));
        String crossChainTopic = EventEncoder.encode(crossChainEvt);
        System.out.println("cross chain signature: " + cutSignature(crossChainTopic));

        filter.addOptionalTopics(crossChainTopic);
        EthLog logs = web3j.ethGetLogs(filter).send();

        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. sender
            String sender = new Address(iterator.next()).getValue();
            System.out.println("Sender: " + sender);

            // 3. token
            String token = new Address(iterator.next()).getValue();
            System.out.println("Token: " + token);

            // 4. non-indexed stuff
            String valueStr = curLogObj.getData();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, crossChainEvt.getNonIndexedParameters());

            String denom = (String) valueDecodeList.get(0).getValue();
            System.out.println("Denom: " + denom);

            String receipt = (String) valueDecodeList.get(1).getValue();
            System.out.println("Receipt: " + receipt);

            BigInteger amount = (BigInteger) valueDecodeList.get(2).getValue();
            System.out.println("Amount: " + amount);

            BigInteger fee = (BigInteger) valueDecodeList.get(3).getValue();
            System.out.println("Fee: " + fee);

            byte[] bytesArr = (byte[]) valueDecodeList.get(4).getValue();
            int nonZeroIdx = 0;
            while (nonZeroIdx < bytesArr.length) {
                if (bytesArr[nonZeroIdx] == 0) {
                    break;
                }
                nonZeroIdx++;
            }
            byte[] conciseArr = new byte[nonZeroIdx];
            System.arraycopy(bytesArr, 0, conciseArr, 0, nonZeroIdx);

            ByteBuffer buffer = ByteBuffer.wrap(conciseArr);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            String targetVal = new String(conciseArr, StandardCharsets.UTF_8);
            System.out.println("Target: " + targetVal);

            String memo = (String) valueDecodeList.get(5).getValue();
            System.out.println("Memo: " + memo);

            System.out.println();
        });
    }

    @Test
    public void crossChainFuncDecoding() throws IOException {

        Function crossChainFunc = new Function("crossChain",
                Arrays.asList(
                        Address.DEFAULT,
                        Utf8String.DEFAULT,
                        Uint256.DEFAULT,
                        Uint256.DEFAULT,
                        Bytes32.DEFAULT,
                        Utf8String.DEFAULT
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));
        String funcSig = FunctionEncoder.encode(crossChainFunc);
        System.out.println("Function signature: " + cutSignature(funcSig));


        EthTransaction txn = web3j.ethGetTransactionByHash("0x648d869985a0b6fd444b9ef32fcd528f7bd446cc1f4ccf8203f8a81adc348a44").send();
        String input = txn.getTransaction().get().getInput();


        // how to decode transaction raw input
        List<Type> decode = FunctionReturnDecoder.decode(input.substring(10),
                Utils.convert(
                        Arrays.asList(
                                TypeReference.create(Address.class),
                                TypeReference.create(Utf8String.class),
                                TypeReference.create(Uint256.class),
                                TypeReference.create(Uint256.class),
                                TypeReference.create(Bytes32.class),
                                TypeReference.create(Utf8String.class)
                        )));

        // 2. sender
        String sender = ((Address) decode.get(0)).getValue();
        System.out.println("Sender: " + sender);

        String receiver = ((Utf8String) decode.get(1)).getValue();
        System.out.println("Receiver: " + receiver);

        BigInteger amount = ((Uint256) decode.get(2)).getValue();
        System.out.println("Amount: " + amount);

        BigInteger fee = ((Uint256) decode.get(3)).getValue();
        System.out.println("Fee: " + fee);

        byte[] bytesArr = ((Bytes32) decode.get(4)).getValue();
        int nonZeroIdx = 0;
        while (nonZeroIdx < bytesArr.length) {
            if (bytesArr[nonZeroIdx] == 0) {
                break;
            }
            nonZeroIdx++;
        }
        byte[] conciseArr = new byte[nonZeroIdx];
        System.arraycopy(bytesArr, 0, conciseArr, 0, nonZeroIdx);

        ByteBuffer buffer = ByteBuffer.wrap(conciseArr);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        String targetVal = new String(conciseArr, StandardCharsets.UTF_8);
        System.out.println("Target: " + targetVal);

        String memo = ((Utf8String) decode.get(5)).getValue();
        System.out.println("Memo: " + memo);

        System.out.println();
    }

    @Test
    public void estimateTxnFee() throws IOException {

        // data
        Function crossChainFunc = new Function("crossChain",
                Arrays.asList(
                        new Address("0x3515f25ab7637adcf1b69f4d384ed5936b83431f"), // token
                        new Utf8String("0x70076F9f8e221d4729314f99a8AB410C117560aB"), // receipt
                        new Uint256(1532000L), // amount
                        new Uint256(10746121L), // fee
                        new Bytes32(strToLittleEndianBytes32("eth")), // your destination
                        Utf8String.DEFAULT // memo
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));

        String data = FunctionEncoder.encode(crossChainFunc);

        // get nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                "0x70076F9f8e221d4729314f99a8AB410C117560aB", DefaultBlockParameterName.PENDING).send();
        BigInteger transactionCount = ethGetTransactionCount.getTransactionCount();
        System.out.println("Nonce: " + transactionCount);

        // get estimation fee
        Transaction transaction = Transaction.createFunctionCallTransaction("0x70076F9f8e221d4729314f99a8AB410C117560aB",
                transactionCount, new BigInteger("500000000000000000"), new BigInteger("1000000"), "0x0000000000000000000000000000000000001004", data);
        EthEstimateGas estimateGas = web3j.ethEstimateGas(transaction).send();
        System.out.println("Estimate Gas: " + estimateGas.getAmountUsed());

    }


    @Test
    public void sendCrossChain_FxEvm2Others() throws IOException {

        String mnemonic = "";
        Credentials credentials = WalletUtils.loadBip39Credentials("", mnemonic);

        Function crossChainFunc = new Function("crossChain",
                Arrays.asList(
                        new Address("0x3515f25ab7637adcf1b69f4d384ed5936b83431f"), // token
                        new Utf8String("0x70076F9f8e221d4729314f99a8AB410C117560aB"), // receipt
                        new Uint256(1532000L), // amount
                        new Uint256(10746121L), // fee
                        new Bytes32(strToLittleEndianBytes32("eth")), // your destination
                        Utf8String.DEFAULT // memo
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));

        String data = FunctionEncoder.encode(crossChainFunc);
        System.out.println("crossChain function encoded data: " + data);

        // get nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                "0x70076F9f8e221d4729314f99a8AB410C117560aB", DefaultBlockParameterName.PENDING).send();
        BigInteger transactionCount = ethGetTransactionCount.getTransactionCount();
        System.out.println("Nonce: " + transactionCount);

        EthGetBalance send = web3j.ethGetBalance("0x70076F9f8e221d4729314f99a8AB410C117560aB", DefaultBlockParameterName.LATEST).send();
        System.out.println("Balance: " + send.getBalance());

        // call contract
        constructAndCallingContractFunction(transactionCount.add(BigInteger.ONE), data, "0x0000000000000000000000000000000000001004", credentials);
    }

    @Test
    public void checkTxn() throws IOException {
        EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt("0x800c3ef87b2848c6e6cccea4d4fa749cdc8255ccb55abb97f9ba1f45419c7d31").send();
        System.out.println(send.getTransactionReceipt().get());
    }


    private static String cutSignature(String wholeSignature) {
        return wholeSignature.length() > 10 ? wholeSignature.substring(0, 10) : wholeSignature;
    }

    public static byte[] strToLittleEndianBytes32(String input) {
        // Convert the string to bytes using UTF-8 encoding
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        // Ensure the byte array length matches the Bytes32 size
        byte[] paddedBytes = new byte[Bytes32.MAX_BYTE_LENGTH];
        System.arraycopy(bytes, 0, paddedBytes, 0, Math.min(bytes.length, paddedBytes.length));

        // Reverse the byte order
        ByteBuffer buffer = ByteBuffer.allocate(Bytes32.MAX_BYTE_LENGTH);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(paddedBytes);
        buffer.flip();

        // Get the reversed bytes
        byte[] littleEndianBytes = new byte[buffer.remaining()];
        buffer.get(littleEndianBytes);

        return littleEndianBytes;
    }

    /**
     * Construct txn inputs & execute, for some reason, the nonce could not be correctly get from web3j.ethGetTransactionCount
     */
    private void constructAndCallingContractFunction(BigInteger nonce, String data, String callingContract, Credentials credentials) throws IOException {
        // another stuff need to be filled
        long chainId = 90001; // for fx-evm
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(60_000_000_000L);
        BigInteger maxFeePerGas = BigInteger.valueOf(70_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(100_000_000L);
        // for interact with contract, value have to input 0
        BigInteger value = BigInteger.valueOf(0L);
        RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, callingContract, value, data, maxPriorityFeePerGas, maxFeePerGas);
        byte[] signedMsg = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMsg);

        System.out.println(maxPriorityFeePerGas.multiply(gasLimit));
        System.out.println(maxFeePerGas.multiply(gasLimit));

        String txHash = Hash.sha3(hexValue);
        System.out.println("OffChain txHash: " + txHash);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        if (ethSendTransaction.hasError()) {
            System.out.println("Error received: " + Keys.toChecksumAddress(ethSendTransaction.getTransactionHash()));
        } else {
            System.out.println("OnChain txHash: " + Keys.toChecksumAddress(ethSendTransaction.getTransactionHash()));
        }
    }

}
