// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract EnumDemo {

    enum ActionChoices {GoLeft, GoRight, GoStraight, SitStill}
    ActionChoices choice;
    ActionChoices constant defaultChoice = ActionChoices.GoStraight;

    function setGoStraight() public {
        choice = ActionChoices.GoStraight;
    }

    // getChoice() would returns (uint8) -> enum would not returned
    function getChoice() public view returns (ActionChoices) {
        return choice;
    }

    function getDefaultChoice() public pure returns (uint) {
        return uint(defaultChoice);
    }
}
