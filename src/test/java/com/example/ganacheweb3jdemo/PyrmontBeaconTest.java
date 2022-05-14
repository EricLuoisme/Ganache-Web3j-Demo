package com.example.ganacheweb3jdemo;

import org.junit.jupiter.api.Test;
import org.web3j.eth2.api.BeaconNodeApi;
import org.web3j.eth2.api.BeaconNodeClientFactory;
import org.web3j.eth2.api.BeaconNodeService;
import org.web3j.eth2.api.beacon.BeaconResource;
import org.web3j.eth2.api.beacon.blocks.BlocksResource;
import org.web3j.eth2.api.config.ConfigResource;
import org.web3j.eth2.api.events.EventsResource;
import org.web3j.eth2.api.schema.BeaconResponse;
import org.web3j.eth2.api.schema.NamedBlockId;
import org.web3j.eth2.api.schema.SignedBeaconBlock;

/**
 * @author Roylic
 * 2022/5/12
 */
public class PyrmontBeaconTest {


    private static final String PYRMOUNT_NODE_URL = "https://29311Fem0enHeQD0BZEoxsxjo37:8be9b626577356ca4fe1dc3cf669d715@eth2-beacon-pyrmont.infura.io";

    @Test
    public void simpleConnection() {


        BeaconNodeService beaconNodeService = new BeaconNodeService(PYRMOUNT_NODE_URL);
        BeaconNodeApi client = BeaconNodeClientFactory.build(beaconNodeService);

        BeaconResource beacon = client.getBeacon();
        BlocksResource blocks = beacon.getBlocks();
        BeaconResponse<SignedBeaconBlock> byId = blocks.findById(NamedBlockId.FINALIZED);
        SignedBeaconBlock data = byId.getData();

        System.out.println(data);
    }

}
