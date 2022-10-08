package com.example.web3j.combination.optimism;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * Optimism
 *
 * @author Roylic
 * 2022/9/23
 */
public class OptimismTest {

    private static final String web3Url = "https://opt-goerli.g.alchemy.com/v2/h_8ml7t3FxTmkRzww_NvLqiw0ZbusFwN";

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

}
