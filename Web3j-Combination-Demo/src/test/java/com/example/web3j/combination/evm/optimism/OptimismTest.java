package com.example.web3j.combination.evm.optimism;

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
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Optimism, gasPrice only available on Mainnet
 *
 * @author Roylic
 * 2022/9/23
 */
public class OptimismTest {

    private static final String web3Url_l1 = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";
    //    private static final String web3Url = "https://opt-goerli.g.alchemy.com/v2/SpOc-iVOBJaN_rdUndJ3P7d7FiRqSPUD";
    private static final String web3Url = "https://opt-mainnet.g.alchemy.com/v2/";

    public static final Web3j web3j_l1 = Web3j.build(new HttpService(web3Url_l1));
    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));


    private static final String address = "0xe10eE98bB84B2073B88353e3AB4433916205DF40";
    private static final String address_2 = "0x7FaAf5cCd7c3C8516c8169df722a108Ec32f73F7";

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
        EthGetBalance balance = web3j.ethGetBalance(address_2, DefaultBlockParameterName.LATEST).send();
        System.out.println(balance.getBalance());
    }

    @Test
    public void test() {
        String s = "lnbc52840n1p3n2ja9pp5lc8kwmcpsmpx6js8a0gv00ar36c7fqmxvwsx70090wxpn2euthqsdqqcqzpgxqzfvsp5ur99l7vpxeaz9ta576h93yulwrj97s3ffdvlesyua05wtafty3qq9qyyssqxa9stjyecmd5gaqjvwtwhqtdvs2ygk208egd3ca3v2y97rayufl8e83rj2wrd7u8ftva37rhke9e58tgcxcanstws782dzfek832ydspmnfc5g";
        System.out.println(s.length());
    }

    @Test
    public void decodeBlockCheckTxn() throws IOException {

        // by using the example on Op-Goerli, https://goerli-optimism.etherscan.io/tx/0x5d0eecd491ac48e19af97197e9991fc488850072b7dc7c30f15c3c3adedcbf9b
        // for optimism, it's block = txn, one block would only contain one txn
        EthBlock ethBlock = web3j.ethGetBlockByNumber(
                DefaultBlockParameter.valueOf(new BigInteger("2481629")), true).send();

        // multiple txn would become a batch, put into the L1-Ethereum
        // with txHash: 0x6878c0e3ce41faefa42bfd35bd14748b116ef9602b3799b13e6d1c3415e671b4
        EthBlock ethBlock_l1 = web3j_l1.ethGetBlockByNumber(
                DefaultBlockParameter.valueOf(new BigInteger("7902863")), true).send();

        List<EthBlock.TransactionResult> transactions = ethBlock_l1.getBlock().getTransactions();
        List<EthBlock.TransactionResult> batchInSingleTxn = transactions.stream().filter(txn -> {
            EthBlock.TransactionObject obj = (EthBlock.TransactionObject) txn;
            return "0x6878c0e3ce41faefa42bfd35bd14748b116ef9602b3799b13e6d1c3415e671b4".equals(obj.get().getHash());
        }).collect(Collectors.toList());

        System.out.println();
    }

    @Test
    public void approveLogDecode() throws IOException {

        BigInteger blockHeight = new BigInteger("11160241");

        // filter
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(blockHeight),
                DefaultBlockParameter.valueOf(blockHeight),
                Collections.emptyList());

        // approve event
        Event approveEvt = new Event("Approval",
                Arrays.asList(
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Address.class, true),
                        TypeReference.create(Uint256.class)));
        String encodeEvt = EventEncoder.encode(approveEvt);
        // add into filter
        filter.addOptionalTopics(encodeEvt);

        // get log and decode
        EthLog logs = web3j.ethGetLogs(filter).send();
        logs.getLogs()
                .forEach(
                        sinEthLog -> {

                            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
                            Iterator<String> topicIterator = curLogObj.getTopics().iterator();

                            // 1. function signature
                            System.out.println("Signature: " + topicIterator.next().substring(0, 10));

                            // 2. owner address
                            String ownerAddress = new Address(topicIterator.next()).getValue();
                            System.out.println("Owner address: " + ownerAddress);

                            // 3. spender address (another contract)
                            String spenderAddress = new Address(topicIterator.next()).getValue();
                            System.out.println("Spender address: " + spenderAddress);

                            // 4. non-indexed stuff in data -> value
                            List<Type> nonIndexedParams = FunctionReturnDecoder.decode(curLogObj.getData(), approveEvt.getNonIndexedParameters());
                            Uint256 val256 = (Uint256) nonIndexedParams.get(0);
                            System.out.println("Approval amount: " + val256.getValue());
                        });

    }

    @Test
    public void decodeSpecificBlock() throws IOException {

        String blockHeight = "107938697";
        String txnHash = "0x49c1c804871abfc2e3a5d94fcde3f0da3336891c2570ec6157c27d1b1ede7689";

        // block
        EthBlock ethBlock = web3j.ethGetBlockByNumber(
                DefaultBlockParameter.valueOf(new BigInteger(blockHeight)), true).send();
        EthBlock.Block block = ethBlock.getBlock();
        List<EthBlock.TransactionResult> collect = block.getTransactions().stream()
                .filter(txn -> txnHash.equalsIgnoreCase(((EthBlock.TransactionObject) txn).getHash()))
                .collect(Collectors.toList());

        // log
        Event transferEvent = new Event("Transfer",
                Arrays.asList(
                        // from
                        TypeReference.create(Address.class),
                        // to
                        TypeReference.create(Address.class),
                        // amount / tokenId
                        TypeReference.create(Uint256.class)
                ));
        // encode event to topic
        String transferTopic = EventEncoder.encode(transferEvent);
        // add topic into filter
        EthFilter ethFilter = new EthFilter(
                new DefaultBlockParameterNumber(new BigInteger(blockHeight)),
                new DefaultBlockParameterNumber(new BigInteger(blockHeight)),
                Collections.emptyList());
        ethFilter.addOptionalTopics(transferTopic);
        // send request
        EthLog logResult = web3j.ethGetLogs(ethFilter).send();
        List<EthLog.LogResult> collect1 = logResult.getLogs().stream()
                .filter(log -> txnHash.equalsIgnoreCase(((EthLog.LogObject) log).getTransactionHash()))
                .collect(Collectors.toList());
        System.out.println();
    }

}
