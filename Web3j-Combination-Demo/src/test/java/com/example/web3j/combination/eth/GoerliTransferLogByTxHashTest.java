package com.example.web3j.combination.eth;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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


    @Test
    public void getLogByTxHashReceiptTest() throws IOException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String txHash = "0xd1feaef0379bbba0045ea8e2925877bc05ff1527d84399860a040ae4197c492e";

        // find txn first
        CompletableFuture<EthGetTransactionReceipt> txnReceiptFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return web3j.ethGetTransactionReceipt(txHash).send();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });

        CompletableFuture<EthTransaction> txnFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return web3j.ethGetTransactionByHash(txHash).send();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });

        try {
            TransactionReceipt transactionReceipt = txnReceiptFuture.get().getTransactionReceipt().get();
            Transaction transaction = txnFuture.get().getTransaction().get();
            EthBlock ethBlock = web3j.ethGetBlockByHash(transactionReceipt.getBlockHash(), false).send();
            BigInteger gasUsed = transactionReceipt.getGasUsed();
            BigInteger transactionFee = transaction.getGasPrice().multiply(gasUsed);


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
                        System.out.println("Block Height: " + ethBlock.getBlock().getNumber());
                        System.out.println("Block Hash: " + ethBlock.getBlock().getHash());
                        System.out.println("Block Time: " + timestamp);
                        System.out.println("Transaction Fee: " + new BigDecimal(transactionFee).divide(BigDecimal.TEN.pow(18)).toPlainString());
                        System.out.println();
                    });

            stopWatch.stop();
            System.out.println("PERFORMANCE: " + stopWatch);


        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("TxHash: ");
        String txHash = in.nextLine();
        checkForErc20Support(txHash);
    }

    /**
     * Check a contract is valid ERC-20 supported
     *
     * @param txHash txHash for the specific ERC-20 was transferred
     */
    public static void checkForErc20Support(String txHash) throws ExecutionException, InterruptedException, IOException {

        // get txn first
        CompletableFuture<EthTransaction> txnFuture = web3j.ethGetTransactionByHash(txHash).sendAsync();
        EthTransaction ethTransaction = txnFuture.get();
        Transaction transaction = ethTransaction.getTransaction().get();

        // get decimal
        Function decimalFunc = new Function("decimals", Collections.emptyList(),
                Collections.singletonList(TypeReference.create(Uint8.class)));

        String decimalFuncSignature = FunctionEncoder.encode(decimalFunc);
        org.web3j.protocol.core.methods.request.Transaction reqTxn =
                org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                        transaction.getTo(), transaction.getTo(), decimalFuncSignature);

        CompletableFuture<EthCall> decimalFuture = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).sendAsync();


        // get block & receipt
        CompletableFuture<EthBlock> blockFuture = web3j.ethGetBlockByHash(transaction.getBlockHash(), false).sendAsync();
        CompletableFuture<EthGetTransactionReceipt> receiptFuture = web3j.ethGetTransactionReceipt(txHash).sendAsync();

        // get log
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
                transaction.getTo());
        ethFilter.addSingleTopic(EventEncoder.encode(transfer));

        // filter log
        EthLog ethLog = web3j.ethGetLogs(ethFilter).send();
        ethLog.getLogs().stream()
                .filter(sinEthLog -> ((EthLog.LogObject) sinEthLog).getTransactionHash().equals(transaction.getHash()))
                .forEach(sinEthLog -> {
                    EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
                    List<String> topics = curLogObj.getTopics();
                    String fromAddress = topics.get(1);
                    String toAddress = topics.get(2);
                    List<Type> decode = FunctionReturnDecoder.decode(curLogObj.getData(), transfer.getNonIndexedParameters());
                    Uint256 val256 = (Uint256) decode.get(0);

                    // from logs
                    System.out.println("From Address: " + fromAddress);
                    System.out.println("To Address: " + toAddress);
                    System.out.println("Token Transferred: " + Convert.fromWei(val256.getValue().toString(), Convert.Unit.ETHER));

                    try {
                        // from block
                        EthBlock.Block block = blockFuture.get().getBlock();
                        System.out.println("Block Height: " + block.getNumber());
                        System.out.println("Block Hash: " + block.getHash());
                        System.out.println("Block Time: " + block.getTimestamp());

                        // from transaction + receipt
                        TransactionReceipt transactionReceipt = receiptFuture.get().getTransactionReceipt().get();
                        BigInteger gasUsed = transactionReceipt.getGasUsed();
                        BigInteger gasPrice = transaction.getGasPrice();
                        BigInteger transactionFee = gasUsed.multiply(gasPrice);
                        System.out.println("Transaction Raw Fee: " + transactionFee);

                        // from decimal
                        String beforeDecode = decimalFuture.get().getValue();
                        List<Type> decoded = FunctionReturnDecoder.decode(beforeDecode, decimalFunc.getOutputParameters());
                        Uint8 decimals = (Uint8) decoded.get(0);
                        System.out.println("Decimal of the ERC-20: " + decimals.getValue());

                        // from transaction + receipt + decimal
                        System.out.println("Transaction Fee: " + new BigDecimal(transactionFee)
                                .divide(BigDecimal.TEN.pow(decimals.getValue().intValue())).toPlainString());

                        System.out.println();
                        System.out.println();
                        System.out.println("VALID ERC-20 CONTRACT");

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                });
    }

}
