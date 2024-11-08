// SPDX-License-Identifier: MIT
pragma solidity >=0.5.0;

contract SolidityVariable {
    address owner;
    uint storedData; // state variable
    uint public count = 2; // public data, would generate get function automatically
    uint internal iCount = 1; // internal data, only can be accessed by contract (when it's running)

    constructor() public {
        // using state variable
        storedData = 10;
        owner = msg.sender;
    }

    // modifier for intercept illegal function calling
    modifier onlyPositive {
        require(storedData > 0);
        // after 'storedData' < 10, the function would not be executable
        _;
    }

    // before the scope after the function input, modifier can be placed
    function getResult() onlyPositive public returns (uint) {
        // local variable (also private variable)
        uint a = 1;
        uint b = 2;
        uint result = a + b + storedData;
        // change state variable
        storedData--;
        return result;
    }
    // use globally available variables
    // view function -> declare that it would not modify the state
    function getSender() public view returns (address) {
        return msg.sender;
    }

    // pure indicates that -> this function does not modify or even read from the state
    // usually would be some calculation helping funcs
    function getPure() public pure returns (bytes32) {
        return "Hehe Pure";
    }

    function callAddMod() public pure returns (uint) {
        return addmod(4, 5, 3);
    }

    function callMulMod() public pure returns (uint){
        return mulmod(4, 5, 3);
    }

    // Solidity support -> keccak256, ripemd160, sha256 & ecrecover
    function callKeccak256() public pure returns(bytes32 result){
        return keccak256("ABC");
    }
}