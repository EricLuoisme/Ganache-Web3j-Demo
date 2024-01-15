package com.example.trick.colUtil;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class TroveUtil {

    public static void main(String[] args) {
        // In Trove, it provides high speed regular & primitive collections
        TIntList tIntList = new TIntArrayList(10);
        tIntList.add(10);
        tIntList.add(10);
        tIntList.add(10);
        tIntList.add(10);
        tIntList.add(10);
        System.out.println(tIntList);
    }
}
