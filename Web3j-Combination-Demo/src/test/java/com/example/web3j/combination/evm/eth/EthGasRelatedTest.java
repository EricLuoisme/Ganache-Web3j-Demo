package com.example.web3j.combination.evm.eth;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Roylic
 * 2023/6/14
 */
public class EthGasRelatedTest {

    private static final String web3Url = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    public static final Web3j web3j = Web3j.build(new HttpService(web3Url));


    @Test
    public void baseGasStuff() throws IOException {

        EthBlock ethBlock = web3j.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(9175837L)), true)
                .send();

        EthBlock.Block block = ethBlock.getBlock();
        BigInteger baseFeePerGas = block.getBaseFeePerGas();
        String baseFeePerGasRaw = block.getBaseFeePerGasRaw();

        AtomicReference<BigInteger> gas = new AtomicReference<>();
        AtomicReference<BigInteger> gasPrice = new AtomicReference<>();
        AtomicReference<BigInteger> maxFeePerGas = new AtomicReference<>();
        AtomicReference<BigInteger> maxPriorityFeePerGas = new AtomicReference<>();

        List<EthBlock.TransactionResult> transactions = block.getTransactions();
        transactions.forEach(txn -> {
            EthBlock.TransactionObject txnObj = (EthBlock.TransactionObject) txn;
            if ("0x4923ab6216c81929b8a72967fc65bb3a75490f9445e2cf279e97663e42eb0feb".equals(txnObj.getHash())) {
                gas.set(txnObj.getGas());
                gasPrice.set(txnObj.getGasPrice());
                maxFeePerGas.set(txnObj.getMaxFeePerGas());
                maxPriorityFeePerGas.set(txnObj.getMaxPriorityFeePerGas());
                System.out.println();
            }
        });

        System.out.println();
    }

}
