// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract ReceiverPays {

    address owner = msg.sender;

    // record all used nonces, prevent replay attack
    mapping(uint256 => bool) usedNonces;

    constructor() payable {

    }


    function claimPayment(uint256 amount, uint256 nonce, bytes memory signature) external {
        require(!usedNonces[nonce]);
        usedNonces[nonce] = true;

        // recreates the message that was signed on the client
        bytes32 message = prefixed(keccak256(abi.encodePacked(msg.sender, amount, nonce, this)));

        // must be signed by owner (contract creator)
        require(recoverSigner(message, signature) == owner);

        // then transfer the amount
        payable(msg.sender).transfer(amount);
    }

    function shutdown() external {
        require(msg.sender == owner);
        selfdestruct(payable(msg.sender));
    }

    function prefixed(bytes32 hash) internal pure returns (bytes32) {
        return keccak256(abi.encodePacked("\x19Ethereum Signed Message:\n32", hash));
    }

    function recoverSigner(bytes32 message, bytes memory sig) internal pure returns (address) {
        (uint8 v, bytes32 r, bytes32 s) = splitSignature(sig);
        return ecrecover(message, v, r, s);
    }

    function splitSignature(bytes memory sig) internal pure returns (uint8 v, bytes32 r, bytes32 s){
        // signature is fixed -> 65 length
        require(sig.length == 65);

        assembly {
            r := mload(add(sig, 32))
            s := mload(add(sig, 64))
            v := byte(0, mload(add(sig, 96))) // final byte (first byte of the next 32 bytes)
        }

        return (v, r, s);
    }


}
