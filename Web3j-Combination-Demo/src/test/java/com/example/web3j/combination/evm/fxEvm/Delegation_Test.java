package com.example.web3j.combination.evm.fxEvm;

import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Delegation_Test {

    private static final String web3Url = "https://testnet-fx-json-web3.functionx.io:8545";

    private static final Web3j web3j = Web3j.build(new HttpService(web3Url));


    @Test
    public void doDelegation() throws IOException {

        String priKeyStr = "";
        Credentials credential = Credentials.create(priKeyStr);
        System.out.println("Derived address: " + credential.getAddress());

        String sender = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String contract = "0x0000000000000000000000000000000000001003";
        String validatorAddress = "fxvaloper1t67ryvnqmnud5g3vpmck00l3umelwkz7huh0s3";
        BigInteger delegateAmt = Convert.toWei(new BigDecimal("10"), Convert.Unit.ETHER).toBigInteger();

        // construct txn
        Function delegateFunc = new Function("delegate",
                Collections.singletonList(new Utf8String(validatorAddress)),
                Collections.singletonList(TypeReference.create(Bool.class))
        );

        String data = FunctionEncoder.encode(delegateFunc);
        System.out.println("delegation function encoded data: " + data);

        // call contract
        constructAndCallingContractFunction(sender, data, delegateAmt, contract, credential);
    }


    @Test
    public void decodeDelegation() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(10438509)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(10438509)),
                Collections.emptyList());

        Event delegateEvt = new Event("Delegate", Arrays.asList(
                TypeReference.create(Address.class, true),
                TypeReference.create(Utf8String.class),
                TypeReference.create(Uint256.class),
                TypeReference.create(Uint256.class)
        ));
        String delegateTopic = EventEncoder.encode(delegateEvt);

        filter.addOptionalTopics(delegateTopic);
        EthLog logs = web3j.ethGetLogs(filter).send();

        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. delegator
            String delegator = new Address(iterator.next()).getValue();
            System.out.println("Delegator: " + delegator);

            // 3. non-indexed stuff
            String valueStr = curLogObj.getData();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, delegateEvt.getNonIndexedParameters());

            String validator = (String) valueDecodeList.get(0).getValue();
            System.out.println("Validator: " + validator);

            BigInteger amount = (BigInteger) valueDecodeList.get(1).getValue();
            System.out.println("Amount: " + Convert.fromWei(amount.toString(), Convert.Unit.ETHER) + " FX");

            BigInteger shares = (BigInteger) valueDecodeList.get(2).getValue();
            System.out.println("Shares: " + shares);
            System.out.println();
        });


    }

    @Test
    public void queryDelegation() throws IOException {
        String sender = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String validatorAddress = "fxvaloper1t67ryvnqmnud5g3vpmck00l3umelwkz7huh0s3";
        String contract = "0x0000000000000000000000000000000000001003";

        Function delegationQueryFunc = new Function("delegation",
                Arrays.asList(new Utf8String(validatorAddress), new Address(sender)),
                Arrays.asList(TypeReference.create(Uint256.class), TypeReference.create(Uint256.class)));
        String encode = FunctionEncoder.encode(delegationQueryFunc);

        Transaction mmCallingTxn = Transaction.createEthCallTransaction(contract, contract, encode);
        EthCall send = web3j.ethCall(mmCallingTxn, DefaultBlockParameterName.LATEST).send();

        // decode
        List<Type> decode = FunctionReturnDecoder.decode(send.getValue(), delegationQueryFunc.getOutputParameters());

        Uint256 shares = (Uint256) decode.get(0);
        System.out.println("Shares  : " + shares.getValue());

        Uint256 delegateAmt = (Uint256) decode.get(1);
        System.out.println("Delegate: " + delegateAmt.getValue());
        System.out.println();
    }


    /**
     * Construct txn inputs & execute, for some reason, the nonce could not be correctly get from web3j.ethGetTransactionCount
     */
    private static void constructAndCallingContractFunction(String senderAddress, String data, BigInteger value,
                                                            String callingContract, Credentials credentials) throws IOException {
        // another stuff need to be filled
        long chainId = 90001; // for fx-evm

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(senderAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        BigInteger gasLimit = new BigInteger("100000");
        BigInteger maxPriorityFeePerGas = Convert.toWei("5", Convert.Unit.GWEI).toBigInteger();
        BigInteger maxFeePerGas = Convert.toWei("600", Convert.Unit.GWEI).toBigInteger();

        System.out.println("maxPriorityFee: " + maxPriorityFeePerGas.multiply(gasLimit));
        System.out.println("maxFee        : " + maxFeePerGas.multiply(gasLimit));

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

    private static byte[] strToLittleEndianBytes32(String input) {
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
