package com.example.web3j.combination.web3j;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LocalGanacheTest {


    private static final String TRUFFLE_ETH_ADDRESS = "http://127.0.0.1:9545";
    private static final String GANACHE_ETH_ADDRESS = "http://127.0.0.1:7545";

    private static final String ADDRESS_0 = "0x2e1d3aE97e79B93f34FF258862eB0f10C3EC542F";
    private static final String PRIVATE_0 = "70cbd0b7e866487bbb41a81a7e8d3752f7c2e49eb618d1aa5a12d3de02150bbc";

    private static final String ADDRESS_1 = "0x010239B52e01484eab6864Ce11511514080d73dA";
    private static final String PRIVATE_1 = "28d340fc45583f73c54f0dcdf8fb1053e2100e9041d0a64693cd5538c3b7c843";

    private static final String ADDRESS_2 = "0x09DB1297Fd5C4FaEe231e325CD1317b0A0F582D1";
    private static final String PRIVATE_2 = "87a07e62623aaebabd2fa25696296684b9c7f8309a04515da2f3795d8a8f2d39";

    private static final String ADDRESS_3 = "0x761ff4BcA3a16612e97971bC1A5D5eF40Eb6529c";
    private static final String PRIVATE_3 = "55443274740cb5388d6a858f68ddeaec69658608de6c5231b55b56790b59bb83";

    private static final String MULTI_TXN_BLOCK_NUM = "72";


    public static void main(String[] args) {

//        connectionChecking(GANACHE_ETH_ADDRESS);

//        transEth(GANACHE_ETH_ADDRESS, PRIVATE_0, ADDRESS_1);

        multiTransEth(GANACHE_ETH_ADDRESS, PRIVATE_0, ADDRESS_1, PRIVATE_2, ADDRESS_3);

//        blockQuery(GANACHE_ETH_ADDRESS, new BigInteger(MULTI_TXN_BLOCK_NUM));

//        checkAccountBalance(GANACHE_ETH_ADDRESS, ADDRESS_0);


    }

    /**
     * Get Eth-Account Balance
     */
    private static void checkAccountBalance(String ethNetAddress, String address) {
        Web3j web3 = Web3j.build(new HttpService(ethNetAddress));
        try {
            EthGetBalance balance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            System.out.println(balance.getBalance());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void blockQuery(String ethNetAddress, BigInteger blockNum) {
        Web3j web3 = Web3j.build(new HttpService(ethNetAddress));
        try {
            EthBlock.Block block = web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNum), true)
                    .send()
                    .getBlock();

            // 常用需要记录的Block信息
            System.out.println("\nRaw Block Infos:");
            System.out.println(block.getDifficulty());
            System.out.println(block.getExtraData());
            System.out.println(block.getGasLimit());
            System.out.println(block.getGasUsed());
            System.out.println(block.getHash());
            System.out.println(block.getLogsBloom());
            System.out.println(block.getMiner());
            System.out.println(block.getNonce());
            System.out.println(block.getNumber());
            System.out.println(block.getParentHash());
            System.out.println(block.getReceiptsRoot());
            System.out.println(block.getSha3Uncles());
            System.out.println(block.getStateRoot());
            System.out.println(block.getTimestamp());
            System.out.println(block.getTransactionsRoot());

            // 通用转换信息
            System.out.println("\nTranslated Block Infos:");
            // 新版本的EthBlock的number, timestamp, 已经是decodeQuantity的了
            System.out.println(block.getNumber());
            System.out.println(block.getParentHash());
            System.out.println(block.getTimestamp().longValue() * 1000);


            System.out.println("\nTransactions in this Block:");
            List<EthBlock.TransactionResult> transactions = block.getTransactions();
            for (EthBlock.TransactionResult transaction : transactions) {
                Object o = transaction.get();
                System.out.println(o);
                /**
                 * hash
                 * nonce
                 * blockHash
                 * blockNumber
                 * transactionIndex
                 * from
                 * to
                 * value
                 * gasPrice
                 * gas
                 * input
                 * creates
                 * publicKey
                 * raw
                 * r
                 * s
                 * v
                 */
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try sending Eth in the Local Private Eth-Net
     */
    private static void sinTransEth(String ethNetAddress, String priKey, String addressTo) {
        Web3j web3 = Web3j.build(new HttpService(ethNetAddress));
        // address exactly the same as ADDRESS_0
        Credentials createdCredential = Credentials.create(priKey);

        // try sending fund
        try {
            Transfer.sendFunds(
                            web3, createdCredential, addressTo, BigDecimal.valueOf(1.0), Convert.Unit.ETHER)
                    .sendAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Try sending Eth in the Local Private Eth-Net
     */
    private static void multiTransEth(String ethNetAddress, String priKey, String addressTo, String priKey2, String addressTo2) {
        Web3j web3 = Web3j.build(new HttpService(ethNetAddress));
        // address exactly the same as ADDRESS_0
        Credentials createdCredential_1 = Credentials.create(priKey);
        Credentials createdCredential_2 = Credentials.create(priKey2);

        // try sending fund
        try {
            CompletableFuture<TransactionReceipt> future1 = Transfer.sendFunds(web3, createdCredential_1, addressTo, BigDecimal.valueOf(1.0), Convert.Unit.ETHER)
                    .sendAsync();
            CompletableFuture<TransactionReceipt> future2 = Transfer.sendFunds(web3, createdCredential_2, addressTo2, BigDecimal.valueOf(1.0), Convert.Unit.ETHER)
                    .sendAsync();

            future1.whenComplete((transactionReceipt, err) -> {
                System.out.println("Trx1 Finished");
            });

            future2.whenComplete((transactionReceipt, err) -> {
                System.out.println("Trx2 Finished");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check connection with local ethereum network
     */
    private static void connectionChecking(String ethNetAddress)  {
        Web3j web3 = Web3j.build(new HttpService(ethNetAddress));  // defaults sto http://localhost:8545/
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println();
            System.out.println(clientVersion);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("-----------------");

        Web3ClientVersion web3ClientVersion2 = null;
        try {
            web3ClientVersion2 = web3.web3ClientVersion().sendAsync().get();
            String clientVersion2 = web3ClientVersion2.getWeb3ClientVersion();
            System.out.println();
            System.out.println(clientVersion2);
            System.out.println();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("===============");

        Web3j web33 = Web3j.build(new HttpService(ethNetAddress));
        web33.web3ClientVersion().flowable().subscribe(x -> {
            String web3ClientVersion3 = x.getWeb3ClientVersion();
            System.out.println();
            System.out.println(web3ClientVersion3);
        });

        System.out.println("~~~~~~~~~~~~~~~~");
    }

}
