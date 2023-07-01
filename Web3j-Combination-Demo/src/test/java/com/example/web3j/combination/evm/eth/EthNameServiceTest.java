package com.example.web3j.combination.evm.eth;

import org.junit.jupiter.api.Test;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class EthNameServiceTest {

    private static final String URL = "https://mainnet.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";
    private static final Web3j web3j = Web3j.build(new HttpService(URL));

    @Test
    public void ensResolveTest() {
        String ensName = "bsc.eth";
        EnsResolver ensResolver = new EnsResolver(web3j);
        String resolve = ensResolver.resolve(ensName);
        System.out.println("Resolve ens: " + ensName + " is for address: " + resolve);
    }

    @Test
    public void ensReverseResolveTest() {
        String address = "0x71cc25e270560ab31d5d2e15e5bf896ebfc4a640";
        EnsResolver ensResolver = new EnsResolver(web3j);
        String ens = ensResolver.reverseResolve(address);
        System.out.println("Reverse resolve address: " + address + " ens domain: " + ens);
    }
}
