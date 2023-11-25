package com.example.trick.reflect;

import java.lang.reflect.Field;

public class ReflectSetting {

    public static class ExampleClass {
        private String a = "init";

        private final String b = "finalized init";

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ExampleClass exampleClass = new ExampleClass();
        System.out.println(exampleClass.getA());

        // reflection change field
        Field field = ExampleClass.class.getDeclaredField("a");
        field.setAccessible(true);
        field.set(exampleClass, "modified");
        System.out.println(exampleClass.getA());

        // try to modify finalized field (NOT WORKING, Reflection pkg prevent you DOING THIS!)
        Field finalField = ExampleClass.class.getDeclaredField("b");
        finalField.setAccessible(true);
        finalField.set(exampleClass, "finalized modified");
        System.out.println(exampleClass.getB());
    }
}
