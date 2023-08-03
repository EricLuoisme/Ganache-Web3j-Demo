// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract SolidityEnums {

    enum FreshJuiceSize{SMALL, MEDIUM, LARGE}
    FreshJuiceSize choice;
    FreshJuiceSize constant defaultChoice = FreshJuiceSize.MEDIUM;

    constructor(){

    }

    function setLarge() public {
        choice = FreshJuiceSize.LARGE;
    }

    function getChoice() public view returns (FreshJuiceSize){
        return choice;
    }

    function getDefaultChoice() public pure returns (uint) {
        return uint(defaultChoice);
    }
}
