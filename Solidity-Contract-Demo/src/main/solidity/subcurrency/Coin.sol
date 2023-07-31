// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract Coin {

    // for all public store variables,
    // solidity would automatically adding public calling func for each or them
    address public minter;
    mapping(address => uint) public balances;

    event Sent(address from, address to, uint amount);

    constructor(){
        minter = msg.sender;
    }

    modifier onlyBy(address _account) {
        require(
            msg.sender == _account,
            "Sender not authorized."
        );
        _;
    }

    function mint(address receiver, uint amount) public onlyBy(minter) {
        balances[receiver] += amount;
    }

    // provide information about why ops failed, they are returned to the caller of the function
    error InsufficientBalance(uint requested, uint available);

    function send(address receiver, uint amount) public {
        if (amount > balances[msg.sender]) {
            revert InsufficientBalance({
            requested : amount,
            available : balances[msg.sender]
            });
        }

        balances[msg.sender] -= amount;
        balances[receiver] += amount;
        // manually emit the event
        emit Sent(msg.sender, receiver, amount);
    }

}
