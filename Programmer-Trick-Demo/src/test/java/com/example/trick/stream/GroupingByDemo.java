package com.example.trick.stream;

import java.util.*;
import java.util.stream.Collectors;

public class GroupingByDemo {

    public static void main(String[] args) {
        List<Item> items = Arrays.asList(
                new Item("key1", "value1"), new Item("key2", "value2"),
                new Item("key1", "value3"), new Item("key2", "value4"),
                new Item("key1", "value1"));

        // by using groupingBy + mapping, we could better traverse & collect the values
        Map<String, Set<String>> collect = items.stream()
                .collect(
                        Collectors.groupingBy(item -> item.key,
                                Collectors.mapping(item -> item.value, Collectors.toSet())));
        System.out.println();
    }

    // Example class
    private static class Item {
        String key;
        String value;

        Item(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
