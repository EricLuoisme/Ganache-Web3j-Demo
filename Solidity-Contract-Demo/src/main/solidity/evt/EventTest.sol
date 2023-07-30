// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract EventTest {

    // declare a event
    event Deposit(address indexed _from, bytes32 indexed _id, uint _value);

    function deposit(bytes32 _id) public payable {
        // emit event
        emit Deposit(msg.sender, _id, msg.value);
    }
}
