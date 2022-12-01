package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author Roylic
 * 2022/12/1
 */
public class GoerliTransferLogByTxHashTest {

    public static final String INFURA_GOERLI_NODE_HTTP_LINK
            = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j = Web3j.build(new HttpService(INFURA_GOERLI_NODE_HTTP_LINK));


    @Test
    public void getLogByTxHashTest() throws IOException {

        // find txn first
        EthTransaction txn = web3j.ethGetTransactionByHash("0xd1feaef0379bbba0045ea8e2925877bc05ff1527d84399860a040ae4197c492e").send();
        Transaction transaction = txn.getTransaction().get();

        // get block for timestamp
        EthBlock ethBlock = web3j.ethGetBlockByHash(transaction.getBlockHash(), false).send();
        // timestamp in second
        BigInteger timestamp = ethBlock.getBlock().getTimestamp();


        Event transfer = new Event("Transfer",
                Arrays.asList(
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        // depends, erc-20 = value, erc-721 = tokenId
                        TypeReference.create(Uint256.class))
        );

        EthFilter ethFilter = new EthFilter(
                DefaultBlockParameter.valueOf(transaction.getBlockNumber()),
                DefaultBlockParameter.valueOf(transaction.getBlockNumber()),
                "0xba62bcfcaafc6622853cca2be6ac7d845bc0f2dc");
        ethFilter.addSingleTopic(EventEncoder.encode(transfer));

        EthLog send = web3j.ethGetLogs(ethFilter).send();
        send.getLogs().stream()
                .filter(sinEthLog -> ((EthLog.LogObject) sinEthLog).getTransactionHash().equals(transaction.getHash()))
                .forEach(sinEthLog -> {
                    EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
                    List<String> topics = curLogObj.getTopics();
                    String fromAddress = topics.get(1);
                    String toAddress = topics.get(2);
                    List<Type> decode = FunctionReturnDecoder.decode(curLogObj.getData(), transfer.getNonIndexedParameters());
                    Uint256 val256 = (Uint256) decode.get(0);
                    System.out.println("From Address: " + fromAddress);
                    System.out.println("To Address: " + toAddress);
                    System.out.println("Token Transferred: " + Convert.fromWei(val256.getValue().toString(), Convert.Unit.ETHER));
                    System.out.println();
                });


    }


}
