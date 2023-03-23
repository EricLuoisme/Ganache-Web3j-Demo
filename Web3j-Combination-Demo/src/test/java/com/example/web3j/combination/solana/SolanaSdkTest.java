//package com.example.web3j.combination.solana;
//
//import com.google.protobuf.ByteString;
//import com.solana.core.*;
//import com.solana.models.*;
//import com.solana.programs.*;
//import com.solana.vendor.*;
//import org.junit.jupiter.api.Test;
//
//public class SolanaSdkTest {
//
//
//    @Test
//    public void txnTest() {
//        // Decode transaction instructions
//        Transaction transaction = Transaction.readTransaction(Base58.decode(rawTx));
//        List<Instruction> instructions = Programs.decodeInstructions(transaction.getMessage(), transaction.getSignatures());
//        for (Instruction instruction : instructions) {
//            switch (instruction.getProgramIndex()) {
//                case 0: // System program
//                    // Decode system instruction
//                    Sysvar decodedSysvar = Programs.decodeSysvar(instruction.getData());
//                    System.out.println("System instruction: " + decodedSysvar);
//
//                    break;
//                case 1: // Token program
//                    // Decode token instruction
//                    TokenProgram decodedToken = TokenProgram.decode(instruction.getData());
//                    System.out.println("Token instruction: " + decodedToken);
//
//                    break;
//
//                // Add more cases for other programs as needed
//            }
//        }
//    }
