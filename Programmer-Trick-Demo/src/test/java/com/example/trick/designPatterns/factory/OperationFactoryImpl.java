package com.example.trick.designPatterns.factory;

import com.example.trick.designPatterns.factory.impl.AddOperation;
import com.example.trick.designPatterns.factory.impl.DivOperation;
import com.example.trick.designPatterns.factory.impl.MulOperation;
import com.example.trick.designPatterns.factory.impl.SubOperation;
import com.example.trick.designPatterns.factory.inter.Operation;

/**
 * Factory模式的一种实现, 其中包含了策略模式进行路由, 确认后才进行构建
 */
public class OperationFactoryImpl implements OperationFactory {

    @Override
    public Operation getInstance(int choice) throws InvalidOperationException {
        switch (choice) {
            case 1:
                return new AddOperation();
            case 2:
                return new SubOperation();
            case 3:
                return new MulOperation();
            case 4:
                return new DivOperation();
            default:
                throw new InvalidOperationException("Not support operation");
        }
    }
}
