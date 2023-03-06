package com.example.web3j.combination.solana;

import com.google.common.primitives.UnsignedBytes;
import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SolanaParsingTest {

    private static final String ACCOUNT = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";
    private static final String TOKEN_ADDRESS = "Gh9ZwEmdLJ8DscKNTkTqPbNwLNNBjuSzaG9Vp2KGtKJr";
    private static final String TOKEN_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA";
    private static final String SPLAssociatedTokenAccountProgramID = "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL";

    private static final String TOKEN_ACCOUNT = "Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc";

    // Java not original support unsigned bytes array, let alone for unsigned bytes sign-utils
    @Test
    public void parsing() {
        byte[] signedAccBytes = Base58.decode(ACCOUNT);
        for (int i = 0; i < signedAccBytes.length; i++) {
            signedAccBytes[i] = (byte) Byte.toUnsignedInt(signedAccBytes[i]);
        }
        System.out.println();
    }


}
