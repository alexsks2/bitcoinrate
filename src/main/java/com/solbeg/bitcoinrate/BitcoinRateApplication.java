package com.solbeg.bitcoinrate;

import java.io.IOException;
import java.util.Scanner;

public class BitcoinRateApplication {

    public static void main(String[] args) {
        System.out.println("Input a currency code (USD, EUR, GBP, etc.):");

        Scanner sc = new Scanner(System.in);
        String currencyCode = sc.nextLine();

        try {
            JsonParserService.getRate(currencyCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
