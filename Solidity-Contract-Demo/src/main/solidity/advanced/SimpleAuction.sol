// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract SimpleAuction {

    // auction params
    address payable public beneficiary;
    uint public auctionEndTime;

    // auction states
    address public highestBidder;
    uint public highestBid;

    // withdrawals of previous bids
    mapping(address => uint) pendingReturns;

    // demonstrate the end
    bool ended;

    // emit on changes
    event HighestBidIncreased(address bidder, uint amount);
    event AuctionEnded(address winnder, uint amount);

    // errors
    error AuctionAlreadyEnded();
    error BidNotHighEnough(uint highestBid);
    error AuctionNotYetEnded();
    error AuctionEndAlreadyCalled();

    constructor(uint biddingTime, address payable beneficiaryAddress){
        beneficiary = beneficiaryAddress;
        auctionEndTime = block.timestamp + biddingTime;
    }

    // keyword 'payable' is required for the function be able to receive Eth
    function bid() external payable {

        if (block.timestamp > auctionEndTime) {
            revert AuctionAlreadyEnded();
        }

        if (msg.value <= highestBid) {
            revert BidNotHighEnough(highestBid);
        }

        if (highestBid != 0) {
            // store them and let the payer withdraw their Eth themselves
            pendingReturns[highestBidder] += highestBid;
        }

        highestBidder = msg.sender;
        highestBid = msg.value;
        emit HighestBidIncreased(msg.sender, msg.value);
    }

    function withdraw() external returns (bool) {
        uint amount = pendingReturns[msg.sender];
        if (amount > 0) {
            pendingReturns[msg.sender] = 0;

            if (!payable(msg.sender).send(amount)) {
                pendingReturns[msg.sender] = amount;
                return false;
            }
        }
        return true;
    }

    function auctionEnd() external {

        // 1. conditions
        if (block.timestamp < auctionEndTime) {
            revert AuctionNotYetEnded();
        }
        if (ended) {
            revert AuctionEndAlreadyCalled();
        }

        // 2. effect
        ended = true;
        emit AuctionEnded(highestBidder, highestBid);

        // 3. interaction
        beneficiary.transfer(highestBid);
    }

}
