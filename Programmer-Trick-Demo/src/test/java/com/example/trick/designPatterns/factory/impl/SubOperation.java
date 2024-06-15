package com.example.trick.designPatterns.factory.impl;

import com.example.trick.designPatterns.factory.inter.Operation;

public class SubOperation implements Operation {
    @Override
    public double calculate(double num1, double num2) {
        return num1 - num2;
    }
}
