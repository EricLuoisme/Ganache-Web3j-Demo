package com.example.trick.map;

import java.util.EnumMap;

/**
 * Enum Map -> 当具备需要使用ENUM反查内容, 同时ENUM作为Key的时候,
 * 可以考虑使用EnumMap类型, 其内部结构使用array存储, 操作O(n)远比其它map类型更节省空间并且迅速
 */
public class EnumMapUsage {

    enum Level {
        LEVEL_1, LEVEL_2, LEVEL_3
    }

    private static final EnumMap<Level, String> ENUM_MAP = new EnumMap<>(Level.class);

    static {
        ENUM_MAP.put(Level.LEVEL_1, "1");
        ENUM_MAP.put(Level.LEVEL_2, "2");
        ENUM_MAP.put(Level.LEVEL_3, "3");
    }


    public static void main(String[] args) {
        System.out.println(ENUM_MAP.get(Level.LEVEL_1));
        System.out.println(ENUM_MAP.get(Level.LEVEL_2));
        System.out.println(ENUM_MAP.get(Level.LEVEL_3));
    }
}
