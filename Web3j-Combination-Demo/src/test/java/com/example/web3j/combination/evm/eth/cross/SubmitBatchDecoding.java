package com.example.web3j.combination.evm.eth.cross;

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
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.example.web3j.combination.evm.fxEvm.CrossChain_Out_Test.cutSignature;

/**
 * Try decode cross chain contract's income evt
 *
 * @author Roylic
 * 2023/7/14
 */
public class SubmitBatchDecoding {

    private static final Web3j web3j
            = Web3j.build(new HttpService("https://goerli.infura.io/v3/3f0482cf4c3545dbabaeab75f414e467"));


    @Test
    public void decodeSubmitBatchEvt() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(9342210L)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(9342210L)),
                Collections.emptyList());

        Event transactionBatchExecutedEvent = new Event("TransactionBatchExecutedEvent", Arrays.asList(
                TypeReference.create(Uint256.class, true), // batchNonce
                TypeReference.create(Address.class, true), // address
                TypeReference.create(Uint256.class) // eventNonce
        ));
        String transactionBatchExecutedEventEvt = EventEncoder.encode(transactionBatchExecutedEvent);
        System.out.println("Batch Executed Evt signature: " + cutSignature(transactionBatchExecutedEventEvt));


        filter.addOptionalTopics(transactionBatchExecutedEventEvt);
        EthLog logs = web3j.ethGetLogs(filter).send();
        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. batch nonce
            Uint256 batchNonce = new Uint256(Numeric.toBigInt(iterator.next()));
            System.out.println("Batch nonce: " + batchNonce.getValue());

            // 3. address
            String address = new Address(iterator.next()).getValue();
            System.out.println("Address: " + address);

            // 4. amount
            String valueStr = curLogObj.getData();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, transactionBatchExecutedEvent.getNonIndexedParameters());
            Uint256 eventNonce = (Uint256) valueDecodeList.get(0);
            System.out.println("Event Nonce: " + eventNonce.getValue());

            System.out.println();
        });
    }
}