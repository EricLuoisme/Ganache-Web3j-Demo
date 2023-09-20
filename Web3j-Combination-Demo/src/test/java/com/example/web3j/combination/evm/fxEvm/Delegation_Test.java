package com.example.web3j.combination.evm.fxEvm;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

public class Delegation_Test {

    private static final String web3Url = "https://testnet-fx-json-web3.functionx.io:8545";

    private static final Web3j web3j = Web3j.build(new HttpService(web3Url));


    @Test
    public void doDelegation() throws IOException {

        String mnemonic = "";
        String password = "";
        Credentials credential = loadBip44Mnemonic2Credential(mnemonic, password, 0);
        System.out.println("Derived address: " + credential.getAddress());

        String sender = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
        String contract = "0x0000000000000000000000000000000000001003";
        String validatorAddress = "fxvaloper1t67ryvnqmnud5g3vpmck00l3umelwkz7huh0s3";
        BigInteger delegateAmt = Convert.toWei(new BigDecimal("10"), Convert.Unit.ETHER).toBigInteger();

        // construct txn
        Function delegateFunc = new Function("delegate",
                Collections.singletonList(new Address(validatorAddress)),
                Collections.singletonList(TypeReference.create(Bool.class))
        );

        String data = FunctionEncoder.encode(delegateFunc);
        System.out.println("delegation function encoded data: " + data);

        // call contract
        constructAndCallingContractFunction(sender, data, delegateAmt, contract, credential);
    }

    @NotNull
    private static Credentials loadBip44Mnemonic2Credential(String mnemonic, String password, int addressIdx) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        final int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0, addressIdx};
        Bip32ECKeyPair childKeypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
        return Credentials.create(childKeypair);
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

        BigInteger gasLimit = new BigInteger("21000");
        BigInteger maxPriorityFeePerGas = Convert.toWei("1", Convert.Unit.GWEI).toBigInteger();
        BigInteger maxFeePerGas = Convert.toWei("300", Convert.Unit.GWEI).toBigInteger();

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
