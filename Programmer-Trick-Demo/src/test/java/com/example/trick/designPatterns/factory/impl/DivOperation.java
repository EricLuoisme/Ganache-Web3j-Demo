package com.example.trick.designPatterns.factory.impl;

import com.example.trick.designPatterns.factory.inter.Operation;

public class DivOperation implements Operation {
    @Override
    public double calculate(double num1, double num2) {
        if (num2 == 0) {
            throw new ArithmeticException("Cannot divide by 0");
        }
        return num1 / num2;
    }
}
