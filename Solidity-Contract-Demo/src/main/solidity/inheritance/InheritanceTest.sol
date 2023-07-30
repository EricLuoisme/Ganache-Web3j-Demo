// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract FatherCont {

    // private state variable
    uint private data;

    // public state variable
    uint public info;

    constructor(){
        info = 10;
    }

    // private function
    function increment(uint a) private pure returns (uint) {return a + 1;}

    // public function
    function updateData(uint a) public {data = a;}

    function getData() public view returns (uint) {
        return data;
    }

    function compute(uint a, uint b) internal pure returns (uint) {
        return a + b;
    }
}

// inheritance
contract ChildCont is FatherCont {

    uint private result;
    FatherCont private fatherCount;

    constructor() public {
        fatherCount = new FatherCont();
    }

    function getComputeResult() public {
        // can easily calling the internal function in father contract
        // update state variable
        result = compute(3, 5);
    }

    function getResult() public view returns (uint) {
        return result;
    }

    function getData() public view returns (uint) {
        // can easily access father contract's public state variable
        return fatherCount.info();
    }

}
