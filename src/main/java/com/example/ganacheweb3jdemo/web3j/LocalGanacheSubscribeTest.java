package com.example.ganacheweb3jdemo.web3j;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class LocalGanacheSubscribeTest {


    private static final String TRUFFLE_ETH_ADDRESS = "http://127.0.0.1:9545";
    private static final String GANACHE_ETH_ADDRESS = "http://127.0.0.1:7545";

    private static final String ADDRESS_0 = "0x532cFd88EBd8Cb84e4bc639e73efFE91Ac0627f9";
    private static final String PRIVATE_0 = "a73d3e668d4b15ef6da22524f7261b950da7690fec63c145bb8b90218479ae59";

    private static final String ADDRESS_1 = "0x736B0bf654C0F8477b3d58A35D28829f76a35848";
    private static final String PRIVATE_1 = "0cc62d6e00dde286fec3dd04f32b01aae0dca23be88f4d8ac97d69cf3b25c440";

    private static final String ADDRESS_2 = "0xF0A073E63644751714863fc26a47832B4286A570";
    private static final String PRIVATE_2 = "ef7f9fa93d5aa134f4eb5bb963edf07c414159f5d45d780dba1b180e05eabc21";

    private static final String ADDRESS_3 = "0x1Bdf3aBC125E6A8c03A8235431FEfd8Ed6d7c904";
    private static final String PRIVATE_3 = "2dd5d88b3a507ee4ff4f88fb5f18e07dfbb7f09e6933b8b4b61d7cf693ec3722";

    private static final String MULTI_TXN_BLOCK_NUM = "72";


    public static void main(String[] args) throws IOException {

        Web3j web3j = Web3j.build(new HttpService(GANACHE_ETH_ADDRESS));



        // subscribe pending txn, keep sending request
//        web3j.pendingTransactionFlowable().subscribe(tx -> {
//            System.out.println("Transfer : ");
//            System.out.println("From : " + tx.getFrom());
//            System.out.println("To : " + tx.getTo());
//            System.out.println("Amount : " + tx.getValue());
//        });
//
//        // subscribe all new block
//        web3j.blockFlowable(false).subscribe(ethBlock -> {
//            System.out.println("New Block Num : " + ethBlock.getBlock().getNumber());
//        });
//
//        // subscribe all new Txn in one new chaining block
//        web3j.transactionFlowable().subscribe(transaction -> {
//            System.out.println("Amount : " + transaction.getValue());
//        });

        // subscribe the eth-logs
//        web3j.ethLogFlowable(new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, Collections.emptyList()))
//                .subscribe(log -> {
//                    System.out.println();
//                    System.out.println("Log - Block Num : " + log.getBlockNumber());
//                    System.out.println();
//                });

//        Event transferEvent = new Event("Transfer",
//                Arrays.asList(
//                        // from
//                        TypeReference.create(Address.class),
//                        // to
//                        TypeReference.create(Address.class),
//                        // amount / tokenId
//                        TypeReference.create(Uint256.class)
//                ));
//        // encode event to topic
//        String transferTopic = EventEncoder.encode(transferEvent);
//        // add topic into filter
//        EthFilter ethFilter = new EthFilter(new DefaultBlockParameterNumber(89L), new DefaultBlockParameterNumber(89L), Collections.emptyList());
//        ethFilter.addOptionalTopics(transferTopic);
//        // send request
//        EthLog logResult = web3j.ethGetLogs(ethFilter).send();
//        System.out.println(logResult);


//        try {
//            // connect to local eth-net
//            Web3j web3j = Web3j.build(new HttpService(GANACHE_ETH_ADDRESS));
//            // build event
//            Event transferEvent = new Event("Transfer",
//                    Arrays.asList(
//                            // from
//                            TypeReference.create(Address.class),
//                            // to
//                            TypeReference.create(Address.class),
//                            // amount / tokenId
//                            TypeReference.create(Uint256.class)
//                    ));
//            // encode event to topic
//            String transferTopic = EventEncoder.encode(transferEvent);
//            // add topic into filter
//            EthFilter ethFilter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, Collections.emptyList());
//            ethFilter.addOptionalTopics(transferTopic);
//            // send request
//            EthLog logResult = web3j.ethGetLogs(ethFilter).send();
//            System.out.println(logResult);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
}