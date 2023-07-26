// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract SolidityArray {
    constructor(){
    }
    function testArray() public pure {
        uint len = 7;

        // dynamic array
        uint[] memory a = new uint[](7);

        // bytes is same as byte[]
        bytes memory b = new bytes(len);

        assert(a.length == 7);
        assert(b.length == len);

        // access array variable
        a[6] = 8;
        assert(a[6] == 8);

        // static array
        uint[3] memory c = [uint(1), 2, 3];
        assert(c.length == 3);
    }
}
