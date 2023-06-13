package com.example.web3j.combination.evm.eth.beacon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.web3j.eth2.api.BeaconNodeApi;
import org.web3j.eth2.api.BeaconNodeClientFactory;
import org.web3j.eth2.api.BeaconNodeService;
import org.web3j.eth2.api.schema.BeaconResponse;
import org.web3j.eth2.api.schema.NamedBlockId;
import org.web3j.eth2.api.schema.SignedBeaconBlock;

public class BeaconTest {

    private ObjectMapper om = new ObjectMapper();

    private static final String nodeUrl = "https://dry-special-paper.ethereum-goerli.discover.quiknode.pro/2b769a89d976b40e56a1f38ebac60e6a04bb28d2/eth/v1/beacon/genesis";


    @Test
    public void connectionTest() throws JsonProcessingException {

        BeaconNodeService nodeService = new BeaconNodeService(nodeUrl);
        BeaconNodeApi client = BeaconNodeClientFactory.build(nodeService);
        BeaconResponse<SignedBeaconBlock> resp = client.getBeacon().getBlocks().findById(NamedBlockId.HEAD);
        SignedBeaconBlock data = resp.getData();

        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(data));

    }

}
