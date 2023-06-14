package com.example.web3j.combination.evm.eth;

import com.example.web3j.combination.ssl.TrustAllX509CertManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Roylic
 * 2023/6/14
 */
public class EthGasRelatedTest {

    private static final String web3Url = "https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467";

    private static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    private static final ObjectMapper om = new ObjectMapper();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(TrustAllX509CertManager.getSslSocketFactory(), new TrustAllX509CertManager()) // trust all certs
            .hostnameVerifier((s, sslSession) -> true) // trust for all hostname
            .retryOnConnectionFailure(false)
            .build();


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


    @Test
    public void baseFeeAndEstimation_Etherscan() {
        String baseFeeUrl = "https://api.etherscan.io/api?module=gastracker&action=gasoracle";
        Request req = new Request.Builder()
                .url(baseFeeUrl)
                .get()
                .build();
        try (Response resp = okHttpClient.newCall(req).execute()) {
            String respStr = resp.body().string();
            System.out.println(respStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void gasTimeEstimation_Etherscan() {
        BigDecimal totalGas = Convert.toWei("18", Convert.Unit.GWEI);
        String gasTimeEstimationUrl = "https://api.etherscan.io/api?module=gastracker&action=gasestimate&gasprice=%s";
        Request req = new Request.Builder()
                .url(String.format(gasTimeEstimationUrl, totalGas.toPlainString()))
                .get()
                .build();
        try (Response resp = okHttpClient.newCall(req).execute()) {
            String respStr = resp.body().string();
            System.out.println(respStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void gasOracle_BlockNative() {

        String apiKey = "";

        String estimateOracle = "https://api.blocknative.com/gasprices/blockprices";
        Request req = new Request.Builder()
                .url(estimateOracle)
                .header("Authorization", apiKey)
                .get()
                .build();

        try (Response resp = okHttpClient.newCall(req).execute()) {
            String respStr = resp.body().string();
            System.out.println(respStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
