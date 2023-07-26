// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract LedgerBalance {

    mapping(address => uint) public balances;

    constructor(){

    }
    function updateBalance(uint newBalance) public {
        balances[msg.sender] = newBalance;
    }
}
