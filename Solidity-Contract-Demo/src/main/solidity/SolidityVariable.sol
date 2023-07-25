// SPDX-License-Identifier: MIT
pragma solidity >=0.5.0;

contract SolidityVariable {
    uint storedData; // state variable
    uint public count = 2; // public data, would generate get function automatically
    uint internal iCount = 1; // internal data, only can be accessed by contract (when it's running)

    constructor() public {
        // using state variable
        storedData = 10;
    }
    function getResult() public view returns (uint) {
        // local variable (also private variable)
        uint a = 1;
        uint b = 2;
        uint result = a + b + storedData;
        return result;
    }
    // use globally available variables
    function getSender() public view returns (address) {
        return msg.sender;
    }
}