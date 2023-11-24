package com.example.trick.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SortDemo {

    public static void main(String[] args) {
        List<Item> items = Arrays.asList(
                new Item("key1", 1), new Item("key2", 2),
                new Item("key1", 3), new Item("key2", 5),
                new Item("key1", 6));
        items.sort((a, b) -> Math.toIntExact(b.value - a.value));
        System.out.println();
    }


    private static class Item {
        String key;
        Integer value;

        public Item(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }
}
