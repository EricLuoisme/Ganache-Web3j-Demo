package com.own.third.api.alchemy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressTokenBalance {
    private String address;
    private List<TokenBalance> tokenBalances;

    @Data
    public static class TokenBalance {
        private String contractAddress;
        private String tokenBalance;
        // extra
        private BigInteger rawBalance;
    }
}
