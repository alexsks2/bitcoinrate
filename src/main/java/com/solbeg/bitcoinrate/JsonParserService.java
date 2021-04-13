package com.solbeg.bitcoinrate;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class JsonParserService {

    static final String currentRateURL = "https://api.coindesk.com/v1/bpi/currentprice/%s.json";
    static final String historicalRateURL = "https://api.coindesk.com/v1/bpi/historical/close.json?start=%s&end=%s&currency=%s";

    static final String BPI = "bpi";
    static final String RATE = "rate_float";

    public static void getRate(String currencyCode) throws IOException, InterruptedException {

        String sURL = String.format(currentRateURL, currencyCode.toLowerCase());

        HttpResponse<String> response = getResponse(sURL);

        if (response.statusCode() != 200) {
            System.out.println(response.body());
            return;
        }

        JsonObject obj = new GsonBuilder().create().fromJson(response.body(), JsonObject.class).getAsJsonObject(BPI);

        double currentPrice = obj.getAsJsonObject(currencyCode.toUpperCase()).get(RATE).getAsDouble();

        getHistoricalPrice(currencyCode, currentPrice);
    }

    public static void getHistoricalPrice(String currencyCode, double currentPrice) throws IOException, InterruptedException {

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String monthAgo = LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_DATE);

        String sURL = String.format(historicalRateURL, monthAgo, today, currencyCode.toLowerCase());

        HttpResponse<String> response = getResponse(sURL);

        JsonObject obj = new GsonBuilder().create().fromJson(response.body(), JsonObject.class).getAsJsonObject(BPI);

        List<Double> historicalData = new ArrayList<>();

        obj.keySet().forEach(keyStr -> historicalData.add(obj.get(keyStr).getAsDouble()));

        double maxPrice = historicalData
                .stream()
                .mapToDouble(v -> v)
                .max().orElseThrow(NoSuchElementException::new);

        double minPrice = historicalData
                .stream()
                .mapToDouble(v -> v)
                .min().orElseThrow(NoSuchElementException::new);

        System.out.println("Current bitcoin rate: " + currentPrice + " " + currencyCode.toUpperCase());

        System.out.println("Lowest bitcoin rate: " + Math.min(minPrice, currentPrice) + " " + currencyCode.toUpperCase());
        System.out.println("Highest bitcoin rate: " + Math.max(maxPrice, currentPrice) + " " + currencyCode.toUpperCase());

    }

    public static HttpResponse<String> getResponse(String sURL) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sURL))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
