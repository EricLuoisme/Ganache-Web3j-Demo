package com.example.web3j.combination.evm.eth;

import io.reactivex.disposables.Disposable;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * Pending related calling
 *
 * @author Roylic
 * 2022/8/19
 */
public class EthPendingTest {

    public static final Web3j web3j
            = Web3j.build(new HttpService("https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467"));

    @Test
    public void pendingTxns() throws InterruptedException {

        // found out Infura do not support eth_newPendingTransactionFilter, this one is an RPC method
        Disposable subscribe = web3j.pendingTransactionFlowable().subscribe(tx -> {
            System.out.println(tx.getPublicKey());
        });

        subscribe.dispose();

        Thread.sleep(10000);
    }


}
