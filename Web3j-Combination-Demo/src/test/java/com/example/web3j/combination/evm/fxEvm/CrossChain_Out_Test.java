package com.example.web3j.combination.evm.fxEvm;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
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
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

public class CrossChain_Out_Test {

    private static final String web3Url = "https://testnet-fx-json-web3.functionx.io:8545";

    private static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    private final MediaType mediaType = MediaType.parse("application/json");


    @Test
    public void crossChainLogDecoding() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(10452593)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(10452593)),
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


        EthTransaction txn = web3j.ethGetTransactionByHash("0x25b4e5a66d1b255a44ac4e8293fb7adebf8c6179da0b5e851c28578ab11412e5").send();
        org.web3j.protocol.core.methods.response.Transaction transaction = txn.getTransaction().get();
        String input = transaction.getInput();

        BigInteger maxPriorityFeePerGas = transaction.getMaxPriorityFeePerGas();
        BigInteger maxFeePerGas = transaction.getMaxFeePerGas();
        System.out.println("maxPriorityFeePerGas: " + Convert.fromWei(maxPriorityFeePerGas.toString(), Convert.Unit.GWEI) + " GWei");
        System.out.println("maxPriorityFeePerGas raw: " + maxPriorityFeePerGas);
        System.out.println("maxFeePerGas: " + Convert.fromWei(maxFeePerGas.toString(), Convert.Unit.GWEI) + " GWei");
        System.out.println("maxFeePerGas raw: " + maxFeePerGas);
        System.out.println("gas: " + transaction.getGas() + " Wei");

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

        // 2. token
        String token = ((Address) decode.get(0)).getValue();
        System.out.println("Token: " + token);

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

//        String mnemonic = "";
//        String password = "";
//        Credentials credential = loadBip44Mnemonic2Credential(mnemonic, password, 0);

        String priKeyStr = "";
        Credentials credential = Credentials.create(priKeyStr);
        System.out.println("Derived address: " + credential.getAddress());

        String sender = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String tokenContract = "0x3515F25AB7637adcF1b69F4D384ed5936B83431F";
        String crossBridgeContract = "0x0000000000000000000000000000000000001004";

        // estimate bridgeFee
        String bridgeFeeStr = getCrossBridgeFeeStandard("ethereum", tokenContract);

        // construct txn
        Function crossChainFunc = new Function("crossChain",
                Arrays.asList(
                        new Address(tokenContract), // token
                        new Utf8String(sender), // receipt
                        new Uint256(1100000), // amount
                        new Uint256(new BigInteger(bridgeFeeStr)), // fee
                        new Bytes32(strToLittleEndianBytes32("eth")), // your destination
                        Utf8String.DEFAULT // memo
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));

        String data = FunctionEncoder.encode(crossChainFunc);
        System.out.println("crossChain function encoded data: " + data);

        // native balance
        EthGetBalance send = web3j.ethGetBalance(sender, DefaultBlockParameterName.LATEST).send();
        System.out.println("balance: " + send.getBalance());

        // token balance
        Function balanceOfFunc = new Function("balanceOf",
                Collections.singletonList(new Address(sender)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
        String balanceFuncEnc = FunctionEncoder.encode(balanceOfFunc);

        Transaction ethCallTransaction = Transaction.createEthCallTransaction(tokenContract, tokenContract, balanceFuncEnc);
        EthCall ethCallResp = web3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST).send();
        List<Type> decodeList = FunctionReturnDecoder.decode(ethCallResp.getValue(), balanceOfFunc.getOutputParameters());
        System.out.println("Token Balance: " + ((Uint256) decodeList.get(0)).getValue());

        // call contract
        constructAndCallingContractFunction(sender, data, crossBridgeContract, credential);
    }

    @Test
    public void cancelCrossChain() throws IOException {

        String priKeyStr = "";
        Credentials credential = Credentials.create(priKeyStr);
        System.out.println("Derived address: " + credential.getAddress());

        String sender = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String crossBridgeContract = "0x0000000000000000000000000000000000001004";

        // construct txn
        Function cancelCrossChainFunc = new Function("cancelSendToExternal",
                Arrays.asList(
                        new Utf8String("eth"), // chain
                        new Uint256(new BigInteger("2324")) // txId
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));

        String data = FunctionEncoder.encode(cancelCrossChainFunc);
        System.out.println("crossChain function encoded data: " + data);

        // call contract
        constructAndCallingContractFunction(sender, data, crossBridgeContract, credential);
    }

    @Test
    public void cancelCrossChainLogDecoding() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(10453193)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(10453193)),
                Collections.emptyList());

        Event cancelSendToExternalEvt = new Event("CancelSendToExternal", Arrays.asList(
                TypeReference.create(Address.class, true), // sender
                TypeReference.create(Utf8String.class), // chain
                TypeReference.create(Uint256.class) // txID

        ));
        String cancelCrossChainTopic = EventEncoder.encode(cancelSendToExternalEvt);
        System.out.println("cancel cross chain topic: " + cutSignature(cancelCrossChainTopic));

        filter.addOptionalTopics(cancelCrossChainTopic);
        EthLog logs = web3j.ethGetLogs(filter).send();

        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            System.out.println("TxHash: " + curLogObj.getTransactionHash());

            // 1. signature
            iterator.next();

            // 2. sender
            String sender = new Address(iterator.next()).getValue();
            System.out.println("Sender: " + sender);

            // 4. non-indexed stuff
            String valueStr = curLogObj.getData();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, cancelSendToExternalEvt.getNonIndexedParameters());

            String chain = (String) valueDecodeList.get(0).getValue();
            System.out.println("Chain: " + chain);

            BigInteger txId = (BigInteger) valueDecodeList.get(1).getValue();
            System.out.println("TxID: " + txId);
            System.out.println();
        });
    }


    @Test
    public void increaseBridgeFee() throws IOException {

        String priKeyStr = "";
        Credentials credential = Credentials.create(priKeyStr);
        System.out.println("Derived address: " + credential.getAddress());

        String sender = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String tokenContract = "0x3515F25AB7637adcF1b69F4D384ed5936B83431F"; // USDT
        String crossBridgeContract = "0x0000000000000000000000000000000000001004";

        // construct txn
        Function increaseBridgeFeeFunc = new Function("increaseBridgeFee",
                Arrays.asList(
                        new Utf8String("eth"), // chain
                        new Uint256(new BigInteger("2326")), // txId
                        new Address(tokenContract), // token, original token -> 0x0000000000000000000000000000000000000000
                        new Uint256(new BigInteger("1000000")) // increase fee amount, 1 USDT
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));

        String data = FunctionEncoder.encode(increaseBridgeFeeFunc);
        System.out.println("increase bridge fee function encoded data: " + data);

        // call contract
        constructAndCallingContractFunction(sender, data, crossBridgeContract, credential);
    }


    @NotNull
    public static Credentials loadBip44Mnemonic2Credential(String mnemonic, String password, int addressIdx) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        final int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0, addressIdx};
        Bip32ECKeyPair childKeypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
        return Credentials.create(childKeypair);
    }

    /**
     * Construct txn inputs & execute, for some reason, the nonce could not be correctly get from web3j.ethGetTransactionCount
     */
    private static void constructAndCallingContractFunction(String senderAddress, String data, String callingContract, Credentials credentials) throws IOException {
        // another stuff need to be filled
        long chainId = 90001; // for fx-evm

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(senderAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        BigInteger gasLimit = new BigInteger("100000");
        BigInteger maxPriorityFeePerGas = Convert.toWei("5", Convert.Unit.GWEI).toBigInteger();
        BigInteger maxFeePerGas = Convert.toWei("600", Convert.Unit.GWEI).toBigInteger();

        System.out.println("maxPriorityFee: " + maxPriorityFeePerGas.multiply(gasLimit));
        System.out.println("maxFee        : " + maxFeePerGas.multiply(gasLimit));

        // for interact with contract, value have to input 0
        BigInteger value = BigInteger.valueOf(0L);
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                chainId, nonce, gasLimit, callingContract, value, data, maxPriorityFeePerGas, maxFeePerGas);
        byte[] signedMsg = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMsg);

        String txHash = Hash.sha3(hexValue);
        System.out.println("OffChain txHash: " + txHash);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        if (ethSendTransaction.hasError()) {
            System.out.println("Error received: " + ethSendTransaction.getError().getMessage());
        } else {
            System.out.println("OnChain txHash: " + Keys.toChecksumAddress(ethSendTransaction.getTransactionHash()));
            EthGetTransactionReceipt receiptResp = web3j.ethGetTransactionReceipt(ethSendTransaction.getTransactionHash()).send();
            System.out.println(receiptResp.getTransactionReceipt());
        }
    }

    private String getCrossBridgeFeeStandard(String forwardChain, String tokenContract) throws IOException {

        JSONObject param = new JSONObject();
        param.put("chainName", forwardChain);
        param.put("tokenContract", tokenContract);

        RequestBody body = RequestBody.create(param.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url("https://testnet-fx-cross-chain-api.functionx.io/common/queryWithdrawFee")
                .post(body)
                .build();

        String respStr = new OkHttpClient.Builder().build().newCall(request).execute().body().string();
        System.out.println(respStr);

        JSONObject jsonObject = JSONObject.parseObject(respStr);
        return jsonObject.getJSONObject("data").getString("standard");
    }


    public static String cutSignature(String wholeSignature) {
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

}
