// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

// abstract contract
contract AbstractCont {
    function getResult() public view returns (uint);
    // abstract contract can still have implemented functions or declar functions
}

// interface can only declare functions
interface AbstractInt {
    function getAbstractResult() public view returns (uint);
}

// extends one abstract contract, must implement it's all funcs
contract ConcreteCont is AbstractCont {
    function getResult() public view returns (uint) {
        uint a = 1;
        uint b = 2;
        uint result = a + b;
        return result;
    }
}
