// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract RestrictedAccess {

    address public owner = msg.sender;
    uint public creationTime = now;

    // onlyBy => only mentioned caller can call this function
    modifier onlyBy(address _account) {
        require(
            msg.sender == _account,
            "Sender not authorized."
        );
        _;
    }

    // onlyAfter => only be called after certain time period
    modifier onlyAfter(uint _time) {
        require(
            now >= _time,
            "Function called too early."
        );
        _;
    }

    // costs => only if certain value is provided
    modifier costs(uint _amount) {
        require(
            msg.value >= _amount,
            "Not enough Ether provided."
        );
        _;
    }

    function changeOwner(address _newOwner) public onlyBy(owner) {
        owner = _newOwner;
    }

    // combine multiple restriction modifier
    function disown() public onlyBy(owner) onlyAfter(creationTime + 6 weeks) {
        delete owner;
    }

    function forceOwnerChange(address _newOwner) public payable costs(0.00005 ether) {
        owner = _newOwner;
        if (uint(owner) & 0 == 1) return;
    }

}
