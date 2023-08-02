// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract SimplePaymentChannel {

    address payable public sender;
    address payable public recipient;
    uint256 public expiration; // timeout in case the recipient never close the channel

    constructor(address payable recipientAddress, uint256 duration) payable {
        sender = payable(msg.sender);
        recipient = recipientAddress;
        expiration = block.timestamp + duration;
    }

    function close(uint256 amount, bytes memory signature) external {
        require(msg.sender == recipient);
        require(isValidSignature(amount, signature));

        recipient.transfer(amount);
        selfdestruct(sender);
    }

    // extend the expiration time
    function extend(uint256 newExpiration) external {
        require(msg.sender == sender);
        require(newExpiration > expiration);

        expiration = newExpiration;
    }

    // if the timeout is reached, without the recipient closing the channel, then Eth is released back to sender
    function claimTimeout() external {
        require(block.timestamp >= expiration);
        selfdestruct(sender);
    }

    // check the signature is from the payment sender
    function isValidSignature(uint256 amount, bytes memory signature) internal view returns (bool) {
        bytes32 message = prefixed(keccak256(abi.encodePacked(this, amount)));
        return recoverSigner(message, signature) == sender;
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
