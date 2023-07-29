// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract Withdrawal {

    address public richest;
    uint public mostSent;

    mapping(address => uint) pendingWithdrawals;

    constructor() public payable{
        richest = msg.sender;
        mostSent = msg.value;
    }

    function becomeRichest() public payable returns (bool) {
        if (msg.value > mostSent) {
            pendingWithdrawals[richest] += msg.value;
            richest = msg.sender;
            mostSent = msg.value;
            return true;
        }
        return false;
    }

    // instead of calling a 'fallible' function like transfer directly inside the same logic block
    // extract a stored variable like mapping, make the whole logic into separate small units
    // then let the 'fallible' function's calling only happens inside really strict area
    function withdraw() public {
        uint amount = pendingWithdrawals[msg.sender];
        pendingWithdrawals[msg.sender] = 0;
        msg.sender.transfer(amount);
    }
}
