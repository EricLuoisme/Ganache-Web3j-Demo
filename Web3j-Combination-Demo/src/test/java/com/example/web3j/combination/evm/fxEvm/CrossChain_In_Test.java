package com.example.web3j.combination.evm.fxEvm;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roylic
 * 2023/7/13
 */
public class CrossChain_In_Test {

    private static final String web3Url = "https://polygon-mumbai.g.alchemy.com/v2/0AvU4bENYqbsSI6km3CEwrgBbyFY_NZX";

    private static final Web3j web3j = Web3j.build(new HttpService(web3Url));

    @Test
    public void decodeSend2FxEvt() throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(37880651)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(37880651)),
                Collections.emptyList());

        Event sendToFxEvt = new Event("SendToFxEvent", Arrays.asList(
                TypeReference.create(Address.class, true), // token
                TypeReference.create(Address.class, true), // sender
                TypeReference.create(Bytes32.class, true), // destination
                TypeReference.create(Bytes32.class), // targetIBC
                TypeReference.create(Uint256.class), // amount
                TypeReference.create(Uint256.class) // eventNonce
        ));
        String crossChainTopic = EventEncoder.encode(sendToFxEvt);
        System.out.println("Send to fx signature: " + cutSignature(crossChainTopic));

        filter.addOptionalTopics(crossChainTopic);
        EthLog logs = web3j.ethGetLogs(filter).send();

        logs.getLogs().forEach(sinEthLog -> {

            EthLog.LogObject curLogObj = (EthLog.LogObject) sinEthLog;
            Iterator<String> iterator = curLogObj.getTopics().iterator();

            // 1. signature
            iterator.next();

            // 2. sender
            String token = new Address(iterator.next()).getValue();
            System.out.println("Token: " + token);

            // 3. sender
            String sender = new Address(iterator.next()).getValue();
            System.out.println("Sender: " + sender);

            // 4. destination
            String destination = iterator.next();
            System.out.println("Destination: " + destination);

            // 5. non-indexed stuff
            String valueStr = curLogObj.getData();
            List<Type> valueDecodeList = FunctionReturnDecoder.decode(valueStr, sendToFxEvt.getNonIndexedParameters());
            String targetIBC = littleEndianByte32ArrToStr((byte[]) valueDecodeList.get(0).getValue());
            System.out.println("TargetIBC: " + targetIBC);

            BigInteger amount = ((Uint256) valueDecodeList.get(1)).getValue();
            System.out.println("Amount: " + amount);

            BigInteger eventNonce = ((Uint256) valueDecodeList.get(2)).getValue();
            System.out.println("EventNonce: " + eventNonce);
            System.out.println();
        });
    }

    @NotNull
    private String littleEndianByte32ArrToStr(byte[] bytesArr) {
        int nonZeroIdx = 0;
        while (nonZeroIdx < bytesArr.length) {
            if (bytesArr[nonZeroIdx] == 0) {
                break;
            }
            nonZeroIdx++;
        }
        byte[] conciseArr = new byte[nonZeroIdx];
        System.arraycopy(bytesArr, 0, conciseArr, 0, nonZeroIdx);
        ByteBuffer buffer = ByteBuffer.wrap(conciseArr);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        String targetVal = new String(conciseArr, StandardCharsets.UTF_8);
        return targetVal;
    }

    private static String cutSignature(String wholeSignature) {
        return wholeSignature.length() > 10 ? wholeSignature.substring(0, 10) : wholeSignature;
    }
}
