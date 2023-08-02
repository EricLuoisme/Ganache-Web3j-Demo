// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract Ballot {

    // complex type
    struct Voter {
        uint weight;
        bool voted;
        address delegate;
        uint vote;
    }

    struct Proposal {
        bytes32 name;
        uint voteCount;
    }

    address public chairperson;

    // store voter struct for each possible address
    mapping(address => Voter) public voters;

    // dynamically-sized array for proposal struct
    Proposal[] public proposals;

    // create a new ballot to choose one of the 'proposalNames'
    // for the deployment, we should input: bytes32 arrays like:
    // ["0x70726f706f73616c310000000000000000000000000000000000000000000000", "0x70726f706f73616c320000000000000000000000000000000000000000000000"]
    constructor(bytes32[] memory proposalNames){
        chairperson = msg.sender;
        voters[chairperson].weight = 1;

        // for each of the provided proposal names, create a new proposal obj and add it into the array
        for (uint i = 0; i < proposalNames.length; i++) {
            proposals.push(
                Proposal({name : proposalNames[i], voteCount : 0}));
        }
    }

    // give right to voter
    function giveRightToVote(address voter) external {
        require(msg.sender == chairperson, "Only chairperson can give right to vote.");
        require(!voters[voter].voted, "The voter already voted.");
        require(voters[voter].weight == 0);
        voters[voter].weight = 1;
    }

    // delegate the vote
    function delegate(address to) external {
        // assigns reference (for mapping)
        Voter storage sender = voters[msg.sender];
        require(sender.weight != 0, "No right to vote");
        require(!sender.voted, "You already voted");

        require(to != msg.sender, "Self-delegation is no allowed");

        while (voters[to].delegate != address(0)) {
            to = voters[to].delegate;
            require(to != msg.sender, "Loop in delegation");
        }

        Voter storage delegate_ = voters[to];

        require(delegate_.weight >= 1);

        sender.voted = true;
        sender.delegate = to;

        if (delegate_.voted) {
            // if the delegator already voted, add votes number
            proposals[delegate_.vote].voteCount += sender.weight;
        } else {
            // if the delegator did not vote yet, add the weight
            delegate_.weight += sender.weight;
        }
    }

    // give the vote
    function vote(uint proposal) external {
        Voter storage sender = voters[msg.sender];
        require(sender.weight != 0, "No right to vote");
        require(!sender.voted, "You already voted");

        sender.voted = true;
        sender.vote = proposal;
        proposals[proposal].voteCount += sender.weight;
    }

    function winningProposal() public view returns (uint winningProposal_) {
        uint winningVoteCount = 0;
        for (uint p = 0; p < proposals.length; p++) {
            if (proposals[p].voteCount > winningVoteCount) {
                winningVoteCount = proposals[p].voteCount;
                winningProposal_ = p;
            }
        }
    }

    function winnerName() external view returns (bytes32 winnerName_){
        winnerName_ = proposals[winningProposal()].name;
    }
}
