package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

/**
 * @author Roylic
 * 2022/11/11
 */
public class EthTransferTest {

    private String address = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
    private String priKey = "1235b980ac298e1f2228b3b3ca3593df89s07813s231ab0c0879dc6768992a767";


    public static final Web3j web3j = Web3j.build(new HttpService("https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467"));


    @Test
    public void getChainId() throws IOException {
        EthChainId send = web3j.ethChainId().send();
        System.out.println(send.getChainId());
    }

    @Test
    public void checkBalance() throws IOException {
        EthGetBalance send = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        System.out.println(send.getBalance());
    }

    @Test
    public void decodeRecentTxnForGasPriceLimit() throws IOException {
        EthTransaction send = web3j.ethGetTransactionByHash("0x017c636b7c5b67f320b2f20e8c2b7495a72725ede157871475025b4322f32881").send();
        BigInteger gasPrice = send.getTransaction().get().getGasPrice();
        System.out.println("GasPrice: " + gasPrice.toString() + " GWei");
    }


    @Test
    public void startTransfer() throws IOException {

        Scanner in = new Scanner(System.in);
        System.out.println("PriKey: ");
        String priKeyUsing = in.nextLine();

        // address exactly the same as ADDRESS_0
        Credentials credentials = Credentials.create(priKeyUsing);

        // get the next available nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        BigInteger gasPrice = Convert.toWei("50", Convert.Unit.GWEI).toBigInteger();
        BigInteger gasLimit = new BigInteger("30000");
        String toAddress = address.toLowerCase(Locale.ROOT);
        BigInteger value = Convert.toWei("0.000345", Convert.Unit.ETHER).toBigInteger();

        // create our transaction
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddress, value);

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        String txHash = Hash.sha3(hexValue);
        System.out.println("OffChain txHash: " + txHash);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        System.out.println("OnChain txHash: " + ethSendTransaction.getTransactionHash());
    }

    @Test
    public void startTransfer_Eip1559_Sync() throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("PriKey: ");
        String priKeyUsing = in.nextLine();

        Credentials credentials = Credentials.create(priKeyUsing);
        TransactionReceipt transactionReceipt = Transfer.sendFundsEIP1559(web3j, credentials,
                "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5",
                new BigDecimal("10086"), Convert.Unit.GWEI, BigInteger.valueOf(21000L),
                BigInteger.valueOf(2_000_000_000L), BigInteger.valueOf(10_000_000_000L)).send();

        System.out.println("Transaction hash: " + transactionReceipt.getTransactionHash());
    }

    @Test
    public void startTransfer_Erc20_Eip1559_Async() throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("PriKey: ");
        String priKeyUsing = in.nextLine();

        // data for interacting with contract's specific method
        // for method 'transfer', only have two input params
        Function transfer = new Function("transfer",
                Arrays.asList(new Address("0x36F0A040C8e60974d1F34b316B3e956f509Db7e5"), new Uint256(10L)),
                Collections.singletonList(TypeReference.create(Bool.class)));

        // encode the data field
        String data = FunctionEncoder.encode(transfer);

        // another stuff need to be filled
        long chainId = 5; // for Goerli
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
        BigInteger maxFeePerGas = BigInteger.valueOf(50_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(50_000L);
        String contract = "0xBA62BCfcAaFc6622853cca2BE6Ac7d845BC0f2Dc";
        BigInteger value = BigInteger.valueOf(10L);

        // async transfer
        Credentials credentials = Credentials.create(priKeyUsing);
        TransactionManager tm = new RawTransactionManager(web3j, credentials);
        EthSendTransaction ethSendTransaction = tm.sendEIP1559Transaction(chainId, maxPriorityFeePerGas, maxFeePerGas, gasLimit, contract, data, value);
        String result = ethSendTransaction.getResult();

        System.out.println("Transaction hash: " + result);
    }

}
