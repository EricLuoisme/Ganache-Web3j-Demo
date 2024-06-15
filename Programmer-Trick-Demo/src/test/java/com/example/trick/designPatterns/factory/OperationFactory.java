package com.example.trick.designPatterns.factory;

import com.example.trick.designPatterns.factory.inter.Operation;

public interface OperationFactory {
    Operation getInstance(int choice) throws InvalidOperationException;
}
