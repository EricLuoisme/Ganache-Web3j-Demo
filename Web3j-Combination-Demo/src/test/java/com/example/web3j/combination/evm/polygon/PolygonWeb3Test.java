package com.example.web3j.combination.evm.polygon;

import com.example.web3j.combination.web3j.EthLogConstants;
import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Polygon Related
 *
 * @author Roylic
 * 2022/9/22
 */
public class PolygonWeb3Test {

    //    private static final String web3Url = "https://polygon-mumbai.g.alchemy.com/v2/0AvU4bENYqbsSI6km3CEwrgBbyFY_NZX";
    private static final String web3Url = "";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));


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
    public void getLatestHeight() {
        try {
            EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            System.out.println(block.getBlock().getNumber());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPeriodEthLogging() {
        try {

            EthFilter filter = new EthFilter(
                    new DefaultBlockParameterNumber(45056699),
                    new DefaultBlockParameterNumber(45056699 + 50),
                    Collections.emptyList());

            filter.addOptionalTopics(
                    EventEncoder.encode(
                            new Event("Transfer", Arrays.asList(
                                    TypeReference.create(Address.class),
                                    TypeReference.create(Address.class),
                                    TypeReference.create(Uint256.class)
                            ))));

            EthLog ethLog = web3j.ethGetLogs(filter).send();
            System.out.println(ethLog.getLogs());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void decodeSpecificBlock() throws InterruptedException, IOException {

        BigInteger blockNum = new BigInteger("28200019");

        EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNum), true)
                .send()
                .getBlock();

        List<EthBlock.TransactionResult> eip1559Txn = block.getTransactions().stream()
                .filter(transactionResult -> ((EthBlock.TransactionObject) transactionResult.get()).getHash().equals("0x85f8269da59aa4d7873d05a1454e92ca6af54c3ddb0198cec12bc77a211f38a1"))
                .collect(Collectors.toList());


        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(blockNum),
                DefaultBlockParameter.valueOf(blockNum.add(BigInteger.ONE)),
                Collections.emptyList());

        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_20_721));
        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_1155_SINGLE));
        filter.addOptionalTopics(EthLogConstants.EthEventTopics.getTopicStr(EthLogConstants.EthEventTopics.TRANSFER_TOPIC_ERC_1155_BATCH));

        web3j.ethLogFlowable(filter).subscribe(log -> {

            // 输出
            System.out.println("\n\nData for ERC-1155 Batch Transfer >>> ");
            System.out.println("removed >>" + log.isRemoved());
            System.out.println("log index >>" + log.getLogIndex());
            System.out.println("txn index >>" + log.getTransactionIndex());
            System.out.println("txn hash >>" + log.getTransactionHash());
            System.out.println("block hash >>" + log.getBlockHash());
            System.out.println("block num >>" + log.getBlockNumber());
            System.out.println("address >>" + log.getAddress());
            System.out.println("data >>" + log.getData());
            System.out.println("type >>" + log.getType());
            System.out.println("topics >>>> ");
            log.getTopics().forEach(System.out::println);
            System.out.println();
            System.out.println();
        });

    }


}
