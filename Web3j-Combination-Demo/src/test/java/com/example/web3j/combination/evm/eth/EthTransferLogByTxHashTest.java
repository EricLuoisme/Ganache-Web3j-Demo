package com.example.web3j.combination.evm.eth;

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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Roylic
 * 2022/12/1
 */
public class EthTransferLogByTxHashTest {

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

    @Test
    public void getNativeTxnTest() throws IOException {

        String txHash = "0x49e75311432d87747841c7de654bd2df973464ed90c3945e2e4974d8002810c1";
        Transaction transaction = web3j.ethGetTransactionByHash(txHash).send().getTransaction().get();
        TransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt().get();
        EthBlock ethBlock = web3j.ethGetBlockByHash(transactionReceipt.getBlockHash(), false).send();
        BigInteger gasUsed = transactionReceipt.getGasUsed();
        BigInteger transactionFee = transaction.getGasPrice().multiply(gasUsed);

        String fromAddress = transaction.getFrom();
        String toAddress = transaction.getTo();
        BigDecimal txnVal = Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER);

        System.out.println("From Address: " + fromAddress);
        System.out.println("To Address: " + toAddress);
        System.out.println("Native Val: " + txnVal);
        System.out.println("Block Height: " + ethBlock.getBlock().getNumber());
        System.out.println("Block Hash: " + ethBlock.getBlock().getHash());
        System.out.println("Block Time: " + ethBlock.getBlock().getTimestamp());
        System.out.println("Transaction Fee: " + new BigDecimal(transactionFee).divide(BigDecimal.TEN.pow(18)).toPlainString());
        System.out.println();
    }


    @Test
    public void routerByTxHashTest() throws IOException {

        // native
//        String txHash = "0x345317d96c11222fb1b92ade51f8157b9962138343a3c842729d925f02f27dd1";

        // erc-20
        String txHash = "0x49e75311432d87747841c7de654bd2df973464ed90c3945e2e4974d8002810c1";


        Transaction transaction = web3j.ethGetTransactionByHash(txHash).send().getTransaction().get();
        TransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt().get();
        EthBlock ethBlock = web3j.ethGetBlockByHash(transactionReceipt.getBlockHash(), false).send();
        BigInteger gasUsed = transactionReceipt.getGasUsed();
        BigInteger transactionFee = transaction.getGasPrice().multiply(gasUsed);


        // separate into native token transfer or ERC-20 transfer
        boolean isNative = false;
        AtomicReference<String> fromAddress = new AtomicReference<>(transaction.getFrom());
        AtomicReference<String> toAddress = new AtomicReference<>(transaction.getTo());
        String blockNum = ethBlock.getBlock().getNumber().toString();
        String blockHash = ethBlock.getBlock().getHash();
        String blockTimestamp = ethBlock.getBlock().getTimestamp().toString();
        AtomicReference<String> transferredVal = new AtomicReference<>("");
        AtomicReference<String> decimal = new AtomicReference<>("");

        BigDecimal txnVal = Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER);
        if (txnVal.compareTo(BigDecimal.ZERO) > 0) {
            // native transaction
            isNative = true;
            transferredVal.set(txnVal.stripTrailingZeros().toPlainString());

        } else {
            // erc-20 transaction
            isNative = false;
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
                        fromAddress.set(new Address(topics.get(1)).toString());
                        toAddress.set(new Address(topics.get(2)).toString());
                        List<Type> decode = FunctionReturnDecoder.decode(curLogObj.getData(), transfer.getNonIndexedParameters());
                        Uint256 val256 = (Uint256) decode.get(0);

                        try {

                            // from decimal
                            Function decimalFunc = new Function("decimals", Collections.emptyList(),
                                    Collections.singletonList(TypeReference.create(Uint8.class)));

                            String decimalFuncSignature = FunctionEncoder.encode(decimalFunc);
                            org.web3j.protocol.core.methods.request.Transaction reqTxn =
                                    org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                                            transaction.getTo(), transaction.getTo(), decimalFuncSignature);

                            String beforeDecode = web3j.ethCall(reqTxn, DefaultBlockParameterName.LATEST).send().getValue();
                            List<Type> decoded = FunctionReturnDecoder.decode(beforeDecode, decimalFunc.getOutputParameters());
                            Uint8 decimals = (Uint8) decoded.get(0);
                            decimal.set(decimals.getValue().toString());

                            // convert and set token transferred
                            transferredVal.set(val256.getValue().divide(decimals.getValue()).toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }


        System.out.println("From Address: " + fromAddress);
        System.out.println("To Address: " + toAddress);
        System.out.println("Native or Token: " + (isNative ? "Native" : "Token"));
        System.out.println("Transferred: " + txnVal);
        if (!isNative) {
            System.out.println("Decimals: " + decimal);
        }
        System.out.println("Block Height: " + blockNum);
        System.out.println("Block Hash: " + blockHash);
        System.out.println("Block Time: " + blockTimestamp);
        System.out.println("Transaction Fee: " + new BigDecimal(transactionFee).divide(BigDecimal.TEN.pow(18)).toPlainString());
        System.out.println();


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
        System.out.println("Contract Address: " + transaction.getTo());

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
                    String fromAddress = new Address(topics.get(1)).toString();
                    String toAddress = new Address(topics.get(2)).toString();
                    List<Type> decode = FunctionReturnDecoder.decode(curLogObj.getData(), transfer.getNonIndexedParameters());
                    Uint256 val256 = (Uint256) decode.get(0);

                    // from logs
                    System.out.println("From Address: " + fromAddress);
                    System.out.println("To Address: " + toAddress);
                    System.out.println("Token Transferred: " + val256.getValue().toString());

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

                        // from transaction + receipt
                        System.out.println("Transaction Fee: " + new BigDecimal(transactionFee)
                                .divide(BigDecimal.TEN.pow(18)).toPlainString());

                        System.out.println();
                        System.out.println();
                        System.out.println("VALID ERC-20 CONTRACT");

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                });
    }


}
