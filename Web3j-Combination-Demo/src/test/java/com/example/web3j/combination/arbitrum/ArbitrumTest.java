package com.example.web3j.combination.arbitrum;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Arbitrum
 *
 * @author Roylic
 * 2022/9/23
 */
public class ArbitrumTest {

    private static final String web3Url_l1 = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";
    private static final String web3Url = "https://arb-goerli.g.alchemy.com/v2/OYmX5E0ny2dezNXeETpswUgDXzuZdE8w";
    private static final String web3_wss = "wss://arb-goerli.g.alchemy.com/v2/OYmX5E0ny2dezNXeETpswUgDXzuZdE8w";

    public static final Web3j web3j_l1 = Web3j.build(new HttpService(web3Url_l1));
    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    private static final String address = "0xe10eE98bB84B2073B88353e3AB4433916205DF40";


    private Queue<Transaction> respQueue = new LinkedList<>();


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
    public void checkBalance() throws IOException {

        EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        System.out.println(balance.getBalance());

    }


    @Test
    public void decodeBlockTxnOnL2() throws IOException {
        // by using the example on Op-Goerli, https://goerli-optimism.etherscan.io/tx/0x5d0eecd491ac48e19af97197e9991fc488850072b7dc7c30f15c3c3adedcbf9b
        // for optimism, it's block = txn, one block would only contain one txn
        EthBlock ethBlock = web3j.ethGetBlockByNumber(
                DefaultBlockParameter.valueOf(new BigInteger("1081618")), true).send();

        // multiple txn would become a batch, put into the L1-Ethereum
        // with txHash: 0x6878c0e3ce41faefa42bfd35bd14748b116ef9602b3799b13e6d1c3415e671b4
        EthBlock ethBlock_l1 = web3j_l1.ethGetBlockByNumber(
                DefaultBlockParameter.valueOf(new BigInteger("7906585")), true).send();

        List<EthBlock.TransactionResult> transactions = ethBlock_l1.getBlock().getTransactions();
        List<EthBlock.TransactionResult> batchInSingleTxn = transactions.stream().filter(txn -> {
            EthBlock.TransactionObject obj = (EthBlock.TransactionObject) txn;
            return "0x6878c0e3ce41faefa42bfd35bd14748b116ef9602b3799b13e6d1c3415e671b4".equals(obj.get().getHash());
        }).collect(Collectors.toList());

        System.out.println();
    }


    @Test
    public void queryTxnHash() throws IOException {
        String txHash = "0x0e6aed31cfba2483d495eeba20e2cf942c22f31d2dd0e7e1c705f03e05541d78";
        EthTransaction txn = web3j.ethGetTransactionByHash(txHash).send();
        System.out.println();
    }

    @Test
    public void subscribeTxnTest() throws IOException, InterruptedException {

        WebSocketService socketService = new WebSocketService(web3_wss, false);
        Web3j wss = Web3j.build(socketService);

        // start connect
        socketService.connect();

        // listen to specific address's txn (really slow)
        wss.transactionFlowable().subscribe(transaction -> {
            if ("0x77F2022532009c5EB4c6C70f395DEAaA793481Bc".equalsIgnoreCase(transaction.getTo())) {
                System.out.println(transaction.getTransactionIndex());
                System.out.println(transaction.getHash());
                System.out.println(transaction.getFrom());
                System.out.println(transaction.getTo());
                System.out.println(transaction.getValue());
            }
        });

        Thread.currentThread().join();
    }


    @Test
    public void subscribeBlockTest() throws IOException, InterruptedException {
        WebSocketService socketService = new WebSocketService(web3_wss, false);
        Web3j wss = Web3j.build(socketService);
        // start connect
        socketService.connect();
        // listen to specific address's txn (also slow)
        wss.blockFlowable(true).subscribe(block -> {
            List<EthBlock.TransactionResult> transactions = block.getBlock().getTransactions();
            transactions.parallelStream().forEach(txn -> {
                EthBlock.TransactionObject txnObj = (EthBlock.TransactionObject) txn.get();
                if ("0x77F2022532009c5EB4c6C70f395DEAaA793481Bc".equalsIgnoreCase(txnObj.getTo())) {
                    System.out.println(txnObj.getTransactionIndex());
                    System.out.println(txnObj.getHash());
                    System.out.println(txnObj.getFrom());
                    System.out.println(txnObj.getTo());
                    System.out.println(txnObj.getValue());
                }
            });
        });
        Thread.currentThread().join();
    }
}
