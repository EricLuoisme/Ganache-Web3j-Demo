package com.example.web3j.combination.web3j.lightning;

/**
 * Lightning Network 节点监听处理
 *
 * @author Roylic
 * 2022/5/27
 */
public class LightningSubscribe {

    private final static String POLAR_BASE_URL = "https://127.0.0.1:";
    private final static String POLAR_MACAROON_LOC = "/Users/pundix2022/.polar/networks/1/volumes/lnd";

    // Alice
    private final static int ALICE_GRPC_PORT = 10001;
    private final static String ALICE_CERT = POLAR_MACAROON_LOC + "/alice/tls.cert";
    private final static String ALICE_MACAROON = POLAR_MACAROON_LOC + "/alice/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String ALICE_PUB_KEY = "02e5e73d1654251a54709341cbda20f8441a639d6ac24e8f5ff0f2b15ef0aaacb5";

    // Dave
    private final static int DAVE_GRPC_PORT = 10004;
    private final static String DAVE_CERT = POLAR_MACAROON_LOC + "/dave/tls.cert";
    private final static String DAVE_MACAROON = POLAR_MACAROON_LOC + "/dave/data/chain/bitcoin/regtest/admin.macaroon";
    private final static String DAVE_PUB_KEY = "0216ba75f68d5e695c754696751080e98db0f1d8674fa72b1c1df221f25fecc2f5";



    public static void main(String[] args) {






    }
}
