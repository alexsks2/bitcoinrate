package com.solbeg.bitcoinrate;

import java.io.IOException;
import java.util.Scanner;

public class BitcoinRateApplication {

    public static void main(String[] args) {
        System.out.println("Input a currency code (USD, EUR, GBP, etc.):");

        String currencyCode;

        while (true) {
            Scanner sc = new Scanner(System.in);
            currencyCode = sc.nextLine().trim();

            if (currencyCode.length() != 3) {
                System.out.println("Please provide valid 3-letter currency code");
            } else {
                break;
            }

        }

        try {
            JsonParserService.getRate(currencyCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
