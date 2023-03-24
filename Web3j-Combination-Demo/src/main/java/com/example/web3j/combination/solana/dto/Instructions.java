package com.example.web3j.combination.solana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Roylic
 * 2023/2/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instructions {

    // list of ordered indices into message.accountKeys array indicating the program account that executes this instruction
    private List<Integer> accounts;

    // program input data encoded in a base-58 string
    private String data;

    // index into message.accountKeys indicating the program account that executes this instruction
    private Integer programIdIndex;
}
