// SPDX-License-Identifier: MIT
pragma solidity >=0.5.0;

contract SolidityVariable {
    uint storedData; // state variable
    constructor() public {
        // using state variable
        storedData = 10;
    }
    function getResult() public pure returns (uint) {
        // local variable
        uint a = 1;
        uint b = 2;
        uint result = a + b;
        // access local variable
        return result;
    }
    // use globally available variables
    function getSender() public view returns (address) {
        return msg.sender;
    }
}