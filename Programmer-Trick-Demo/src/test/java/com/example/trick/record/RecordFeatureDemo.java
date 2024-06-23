package com.example.trick.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RecordFeatureDemo {


    public static void main(String[] args) {
        AAA a = AAA.builder().featureA("A").build();
        BBB b = BBB.builder().featureB("B").build();
        APackB aPackB = tryPackAAndB(a, b);
        System.out.println(aPackB);
    }


    public static APackB tryPackAAndB(AAA a, BBB b) {
        return new APackB(a, b);
    }


    public record APackB(AAA a, BBB b) {
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AAA {
        private String featureA;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BBB {
        private String featureB;
    }

}
