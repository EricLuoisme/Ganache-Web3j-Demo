package com.example.trick.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

public class RecordFeatureDemo {


    public static void main(String[] args) {
        AAA a = AAA.builder().featureA("A").build();
        BBB b = BBB.builder().featureB("B").build();
        APackB aPackB = tryPackAAndB(a, b);
        System.out.println(APackB.NAME);
        System.out.println(aPackB);
        System.out.println(aPackB.getFeatureA());
    }


    public static APackB tryPackAAndB(AAA a, BBB b) {
        return new APackB(a, b);
    }


    public record APackB(AAA a, BBB b) {

        public static final String NAME = "THIS IS A PACKED RECORD";

        public APackB {
            // 对于Record还可以添加类似的constructor校验
            if (Objects.isNull(a.featureA)) {
                throw new IllegalArgumentException("Could not be null");
            }
        }

        public String getFeatureA() {
            return a.featureA;
        }
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
