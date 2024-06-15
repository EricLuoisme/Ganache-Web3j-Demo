package com.example.trick.designPatterns.factory;

public class InvalidOperationException extends Exception {
    public InvalidOperationException(String msg) {
        super(msg);
    }
}
