package com.example.web3j.combination.evm.fxEvm;

import org.bouncycastle.util.encoders.Hex;
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
import org.web3j.tx.RawTransactionManager;
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

import static com.example.web3j.combination.evm.fxEvm.CrossChain_Out_Test.loadBip44Mnemonic2Credential;
import static com.example.web3j.combination.evm.fxEvm.CrossChain_Out_Test.strToLittleEndianBytes32;

/**
 * @author Roylic
 * 2023/7/13
 */
public class CrossChain_In_Test {

    private static final String web3Url = "https://polygon-mumbai.g.alchemy.com/v2/0AvU4bENYqbsSI6km3CEwrgBbyFY_NZX";

    private static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    @Test
    public void decodeSend2FxEvt() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(37880651)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(37880651)),
                Collections.emptyList());

        Event sendToFxEvt = new Event("SendToFxEvent", Arrays.asList(
                TypeReference.create(Address.class, true), // token
                TypeReference.create(Address.class, true), // sender
                TypeReference.create(Bytes32.class, true), // destination
                TypeReference.create(Bytes32.class), // targetIBC
                TypeReference.create(Uint256.class), // amount
                TypeReference.create(Uint256.class) // eventNonce
        ));
        String crossChainTopic = EventEncoder.encode(sendToFxEvt);
        System.out.println("Send to fx signature: " + cutSignature(crossChainTopic));

        filter.addOptionalTopics(crossChainTopic);
        EthLog logs = web3j.ethGetLogs(filter).send();

        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. sender
            String token = new Address(iterator.next()).getValue();
            System.out.println("Token: " + token);

            // 3. sender
            String sender = new Address(iterator.next()).getValue();
            System.out.println("Sender: " + sender);

            // 4. destination
            String destination = iterator.next();
            System.out.println("Destination: " + destination);

            // 5. non-indexed stuff
            String valueStr = curLogObj.getData();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, sendToFxEvt.getNonIndexedParameters());
            String targetIBC = littleEndianByte32ArrToStr((byte[]) valueDecodeList.get(0).getValue());
            System.out.println("TargetIBC: " + targetIBC);

            BigInteger amount = ((Uint256) valueDecodeList.get(1)).getValue();
            System.out.println("Amount: " + amount);

            BigInteger eventNonce = ((Uint256) valueDecodeList.get(2)).getValue();
            System.out.println("EventNonce: " + eventNonce);
            System.out.println();
        });
    }

    @Test
    public void decodeTxnInputData() throws IOException {

        String txnHash = "0x38343d736ec80c58f861212c83cce18b60968168a2102cef2a0fa834ec59b9f2";

        Function send2FxFunc = new Function("sendToFx",
                Arrays.asList(
                        Address.DEFAULT, // token contract
                        Bytes32.DEFAULT, // destination
                        Bytes32.DEFAULT, // targetIBC
                        Uint256.DEFAULT // amount
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));
        String funcSig = FunctionEncoder.encode(send2FxFunc);
        System.out.println("Function signature: " + cutSignature(funcSig));

        EthTransaction txn = web3j.ethGetTransactionByHash(txnHash).send();
        org.web3j.protocol.core.methods.response.Transaction transaction = txn.getTransaction().get();
        String input = transaction.getInput();
        System.out.println("gas: " + transaction.getGas());
        System.out.println("gas price: " + transaction.getGasPrice());

        // how to decode transaction raw input
        List<Type> decode = FunctionReturnDecoder.decode(input.substring(10),
                Utils.convert(
                        Arrays.asList(
                                TypeReference.create(Address.class),
                                TypeReference.create(Bytes32.class),
                                TypeReference.create(Bytes32.class),
                                TypeReference.create(Uint256.class)
                        )));

        // 1. token contract
        String token = ((Address) decode.get(0)).getValue();
        System.out.println("Token: " + token);

        // 2. destination
        String destination = Hex.toHexString(((Bytes32) decode.get(1)).getValue());
        System.out.println("Destination: " + destination);

        // 3. targetIBC
        String targetIBC = littleEndianByte32ArrToStr(((Bytes32) decode.get(2)).getValue());
        System.out.println("TargetIBC: " + targetIBC);

        // 4. amount
        BigInteger amount = ((Uint256) decode.get(3)).getValue();
        System.out.println("Amount: " + amount);
        System.out.println();
    }

    @Test
    public void send2FxTxnTest() throws IOException {

        String mnemonic = "";
        String password = "";
        Credentials credential = loadBip44Mnemonic2Credential(mnemonic, password, 2);
        System.out.println("Derived address: " + credential.getAddress());

        String sender = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String tokenContract = "0x40212e7bb7f79fe7ff3870cbd55427c01d69d2b7";
        String crossBridgeContract = "0x57b1e4c85b0f141ade38b5573907ba8ef9ac2298";

        // construct txn
        Function send2FxFunc = new Function("sendToFx",
                Arrays.asList(
                        new Address(tokenContract), // token contract
                        new Bytes32(Numeric.hexStringToByteArray("00000000000000000000000036f0a040c8e60974d1f34b316b3e956f509db7e5")), // destination
                        new Bytes32(strToLittleEndianBytes32("erc20")), // targetIBC
                        new Uint256(new BigInteger("3290000")) // amount
                ),
                Collections.singletonList(TypeReference.create(Bool.class)));

        String data = FunctionEncoder.encode(send2FxFunc);
        System.out.println("send2FxFunc function encoded data: " + data);

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

    /**
     * Construct txn inputs & execute, for some reason, the nonce could not be correctly get from web3j.ethGetTransactionCount
     */
    private void constructAndCallingContractFunction(String senderAddress, String data, String callingContract, Credentials credentials) throws IOException {
        // another stuff need to be filled
        long chainId = 80001; // for polygon mumbai

        // get nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(senderAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        BigInteger gasPrice = Convert.toWei("1", Convert.Unit.GWEI).toBigInteger();
        BigInteger gasLimit = new BigInteger("250000");

        // for interact with contract, value have to input 0
        BigInteger value = BigInteger.valueOf(0L);

        RawTransactionManager rawTransactionManager = new RawTransactionManager(web3j, credentials, chainId);
        EthSendTransaction ethSendTransaction = rawTransactionManager.sendTransaction(gasPrice, gasLimit, callingContract, data, value);

        if (ethSendTransaction.hasError()) {
            System.out.println("Error received: " + ethSendTransaction.getError().getMessage());
        } else {
            System.out.println("OnChain txHash: " + Keys.toChecksumAddress(ethSendTransaction.getTransactionHash()));
            EthGetTransactionReceipt receiptResp = web3j.ethGetTransactionReceipt(ethSendTransaction.getTransactionHash()).send();
            System.out.println(receiptResp.getTransactionReceipt());
        }
    }

    @NotNull
    private String littleEndianByte32ArrToStr(byte[] bytesArr) {
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
        return targetVal;
    }

    private static String cutSignature(String wholeSignature) {
        return wholeSignature.length() > 10 ? wholeSignature.substring(0, 10) : wholeSignature;
    }
}
