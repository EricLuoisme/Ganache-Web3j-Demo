package com.example.trick.string;

public class StringIntern {
    /**
     * str.intern() 方法是将该字符串直接放入常量池,
     * 在大数据量的数据 (e.g. Config Values), 但是基本不需要变化的数据
     * 可以考虑直接存入常量池, 避免String都是对象,
     * 以及Nacos变化时, 序列化与反序列化new String操作吃内存
     */
    public static void main(String[] args) {
        String abc = "abc";
        String intern = abc.intern();

        System.out.println(intern);
    }
}
