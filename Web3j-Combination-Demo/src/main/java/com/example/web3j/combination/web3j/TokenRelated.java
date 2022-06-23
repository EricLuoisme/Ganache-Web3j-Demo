package com.example.web3j.combination.web3j;

import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthLog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Roylic
 * @date 2022/4/12
 */
public class TokenRelated {

    public static void main(String[] args) {
        TokenRelated tokenRelated = new TokenRelated();
        tokenRelated.getLogObjects();
    }


    public void getLogObjects() {

        // ERC-721 Log
        EthLog.LogObject logObject_721 = new EthLog.LogObject();
        logObject_721.setRemoved(false);
        logObject_721.setLogIndex("0x2");
        logObject_721.setTransactionIndex("0x0");
        logObject_721.setTransactionHash("0x83729b2e8b203b7fbf492ec13d44c29a2db28b4c569fa546b2ea7379dee341f6");
        logObject_721.setBlockHash("0xe3333874ff155f62128a3a587fe97e50956da6145e04a9edc4a3bfc44f5ae42c");
        logObject_721.setBlockNumber("0x1d4ca9c");
        logObject_721.setAddress("0xf5de760f2e916647fd766b4ad9e85ff943ce3a2b");
        logObject_721.setData("0x");
        logObject_721.setType("mined");
        logObject_721.setTopics(Stream.of(
                        "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                        "0x0000000000000000000000000000000000000000000000000000000000000000",
                        "0x0000000000000000000000006dcdfa62f1c4cae2ea4e0e568d7850408be1435f",
                        "0x00000000000000000000000000000000000000000000000000000000000ca8b5")
                .collect(Collectors.toList()));


        Function function = new Function("tokenId", new ArrayList<>(), Stream.of(TypeReference.create(Uint256.class)).collect(Collectors.toList()));
        List<Type> decode = FunctionReturnDecoder.decode(logObject_721.getTopics().get(3), function.getOutputParameters());

        List<TypeReference<?>> outputParameters = Stream.of(TypeReference.create(Uint256.class))
                .collect(Collectors.toList());

        List<Type> TokenIdList_1 = FunctionReturnDecoder.decode(logObject_721.getTopics().get(3), Utils.convert(outputParameters));
        System.out.println(TokenIdList_1.get(0).getValue());


        // ERC-1155 Single Log
        EthLog.LogObject logObject_1155_single = new EthLog.LogObject();
        logObject_1155_single.setRemoved(false);
        logObject_1155_single.setLogIndex("0xa");
        logObject_1155_single.setTransactionIndex("0x5");
        logObject_1155_single.setTransactionHash("0x88668d859d136057c576915e63b1038707a5af0d8230350c9462a8632f19783e");
        logObject_1155_single.setBlockHash("0x1483a152bd8f91a846df198def6f4655e8abd06ebfd8808fb7f0d36fe505db4b");
        logObject_1155_single.setBlockNumber("0x1d4caa3");
        logObject_1155_single.setAddress("0xcc57b6d9768e05e8cfb6081ec0f1cb4635e1548d");
        logObject_1155_single.setData("0x467fcabdddf8a5e4ddcfb6bc056755e5adfa099b560aca0cfe8afe071e2717050000000000000000000000000000000000000000000000056bc75e2d63100000");
        logObject_1155_single.setType("mined");
        logObject_1155_single.setTopics(Stream.of(
                        "0xc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62",
                        "0x000000000000000000000000723a9bb7abacf54e46b2b31ecfcca3b5921b9d52",
                        "0x0000000000000000000000000000000000000000000000000000000000000000",
                        "0x000000000000000000000000723a9bb7abacf54e46b2b31ecfcca3b5921b9d52")
                .collect(Collectors.toList()));


        // chain node
        String https_url = "https://kovan.infura.io/v3/36ad2ee634ef4b418b4b9fecbbda6883";
    }
}
