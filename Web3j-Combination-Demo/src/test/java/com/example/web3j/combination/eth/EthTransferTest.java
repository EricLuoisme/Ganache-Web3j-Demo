package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Locale;

/**
 * @author Roylic
 * 2022/11/11
 */
public class EthTransferTest {

    private String address = "0x36F0A040C8e60974d1F34b316B3e956f509Db7e5";
    private String priKey = "1235b980ac298e1f2228b3b3ca3593df89s07813s231ab0c0879dc6768992a767";


    public static final Web3j web3j = Web3j.build(new HttpService("https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467"));


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
        // address exactly the same as ADDRESS_0
        Credentials credentials = Credentials.create(priKey);

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


}
