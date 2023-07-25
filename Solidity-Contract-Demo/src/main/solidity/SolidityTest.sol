// SPDX-License-Identifier: MIT
pragma solidity >=0.5.0; // new version of compiler recognize >= rather than ^
contract SolidityTest {
    constructor() public {
    }
    // fixed output should use 'pure'
    function getResult() public pure returns(uint) {
        uint a = 1;
        uint b = 2;
        uint result = a + b;
        return result;
    }
}