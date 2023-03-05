package com.example.web3j.combination.solana;

import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

public class SolanaParsingTest {

    private static final String ACCOUNT = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";
    private static final String TOKEN_ADDRESS = "Gh9ZwEmdLJ8DscKNTkTqPbNwLNNBjuSzaG9Vp2KGtKJr";
    private static final String TOKEN_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA";
    private static final String SPLAssociatedTokenAccountProgramID = "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL";

    private static final String TOKEN_ACCOUNT = "Gd8nxWzbnJ2zwtn5TukvEMKKjjbFhdtqA1L67DgnRvXc";

    @Test
    public void parsing() {
        byte[] accountBytes = Base58.decode(ACCOUNT);
        byte[] tokenAddressBytes = Base58.decode(TOKEN_ADDRESS);

        byte[][] seeds = new byte[3][];
        seeds[0] = accountBytes;
        seeds[1] = Base58.decode(TOKEN_PROGRAM_ID);
        seeds[3] = tokenAddressBytes;



    }


}
