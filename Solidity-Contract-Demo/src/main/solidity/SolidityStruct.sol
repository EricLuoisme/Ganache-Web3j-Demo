// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract SolidityStruct {

    // struct
    struct Book {
        string title;
        string author;
        uint book_id;
    }

    Book book;

    constructor(){
    }

    // new struct
    function setBook() public {
        book = Book("learn Java", "TP", 1);
    }

    // get struct params
    function getBookId() public view returns (uint) {
        return book.book_id;
    }

}
