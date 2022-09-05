package com.example.web3j.combination.eth;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * @author Roylic
 * 2022/7/27
 */
public class FxEvmRelatedTest {


    @Test
    public void fxEvmResolveTest() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpService httpService = new HttpService("https://testnet-fx-json-web3.functionx.io:8545", client, false);
        Web3j web3j = Web3j.build(httpService);

        long blockNum = 4_377_864L;

        blockQuery(web3j, BigInteger.valueOf(blockNum));


        Optional<Transaction> transaction = web3j.ethGetTransactionByHash("0xd977be2ff413e9a624036beed7e1d95b24114d04ec81b23dfe3cff84934369ac").send().getTransaction();
        System.out.println();
    }

    private static void blockQuery(Web3j web3j, BigInteger blockNum) {

        try {
            EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNum), true)
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
