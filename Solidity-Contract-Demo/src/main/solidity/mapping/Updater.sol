// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

import "./LedgerBalance.sol";

contract Updater {
    constructor(){

    }

    function updateBalance() public returns (uint) {
        LedgerBalance ledgerBalance = new LedgerBalance();
        ledgerBalance.updateBalance(10);
        return ledgerBalance.balances(address(this));
    }
}
