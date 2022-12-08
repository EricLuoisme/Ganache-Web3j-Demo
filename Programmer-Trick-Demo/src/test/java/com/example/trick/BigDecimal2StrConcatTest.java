package com.example.trick;

import org.junit.Test;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class BigDecimal2StrConcatTest {


    @Test
    public void wholeProcess() {
        testBigDecimalCalculation();
        testLongCalculation();
    }



    @Test
    public void testBigDecimalCalculation() {

        String amtInputAmtStr = "20.32";
        BigDecimal tipValPercentage = new BigDecimal("0.03456");
        BigDecimal m2cFee = new BigDecimal("0.5");
        String fiatCryptoRate = "0.000170";

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Fiat Calculate");

        BigDecimal amtInput = new BigDecimal(amtInputAmtStr).setScale(6, RoundingMode.CEILING);
        BigDecimal tipsCalculated = amtInput.multiply(tipValPercentage).setScale(6, RoundingMode.CEILING);
        tipsCalculated = tipsCalculated.add(m2cFee);
        BigDecimal amtFiatTotal = amtInput.add(tipsCalculated);

        stopWatch.stop();
        stopWatch.start("Crypto Calculate");

        // really slow
        BigDecimal pureAmtCryptoTotal = amtFiatTotal.multiply(new BigDecimal(fiatCryptoRate)).setScale(6, RoundingMode.CEILING);
        BigDecimal nonceVal = new BigDecimal(get5DigitRand()).divide(BigDecimal.TEN.pow(18));
        BigDecimal amtCryptoTotal = pureAmtCryptoTotal.add(nonceVal);

        stopWatch.stop();
        stopWatch.start("String");


        String amtCryptoTotalStr = amtCryptoTotal.toPlainString();
        String nonce = amtCryptoTotalStr.substring(amtCryptoTotalStr.length() - 5);

        System.out.println("Amt Crypto: " + amtCryptoTotalStr + ", Nonce: " + nonce);
        stopWatch.stop();
        System.out.println("Performance: " + stopWatch);
    }

    @Test
    public void testLongCalculation() {

        String amtInputAmtStr = "20.32";
        double tipValPercentage = 0.03456;
        long m2cFee100 = 50;
        double fiatCryptoRate = 0.000170;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Fiat Calculate");

        long amtInput100 = new BigDecimal(amtInputAmtStr)
                .setScale(2, RoundingMode.CEILING)
                .multiply(BigDecimal.TEN.pow(2))
                .longValue();

        double tipsCalculated100 = amtInput100 * tipValPercentage;
        tipsCalculated100 += m2cFee100;
        long amtFiatTotal100 = (amtInput100 + ((Double) Math.ceil(tipsCalculated100)).longValue());

        stopWatch.stop();
        stopWatch.start("Crypto Calculate");

        BigDecimal pureAmtCryptoTotal = new BigDecimal(((Double) ((amtFiatTotal100 / 100) * fiatCryptoRate)).toString()).setScale(6, RoundingMode.CEILING);
        int digitRand = get5DigitRand();
        BigDecimal nonceVal = new BigDecimal(digitRand).divide(BigDecimal.TEN.pow(18));

        stopWatch.stop();
        stopWatch.start("String");

        String firstHalf = pureAmtCryptoTotal.toPlainString();
        String lastHalf = nonceVal.toPlainString();
        String amtCryptoTotalStr = firstHalf + lastHalf.substring(8);

        System.out.println("Amt Crypto: " + amtCryptoTotalStr + ", Nonce: " + digitRand);
        stopWatch.stop();
        System.out.println("Performance: " + stopWatch);
    }


    private int get5DigitRand() {
        return 1000 + ThreadLocalRandom.current().nextInt(9000);
    }

}
