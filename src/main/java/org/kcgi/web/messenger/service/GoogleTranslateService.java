package org.kcgi.web.messenger.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GoogleTranslateService {

    private static final String API_KEY = "AIzaSyCGEt2fjwcks9PViG7iKkues7OhFE4Fa1U"; // Use environment variable for production
    private static final String TRANSLATE_URL = "https://translation.googleapis.com/language/translate/v2";

    /**
     * Translates a message from the source language to the target language using Google Translate API.
     *
     * @param message        The text to translate.
     * @param sourceLanguage The source language code (e.g., "en").
     * @param targetLanguage The target language code (e.g., "ja").
     * @return The translated text.
     * @throws Exception If an error occurs during translation.
     */
    public static String translateMessage(String message, String sourceLanguage, String targetLanguage) throws Exception {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message to translate cannot be null or empty");
        }

        URL url = new URL(TRANSLATE_URL + "?key=" + API_KEY);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");

        // Create the JSON payload
        JsonObject payload = new JsonObject();
        payload.addProperty("q", message);
        payload.addProperty("source", sourceLanguage);
        payload.addProperty("target", targetLanguage);
        payload.addProperty("format", "text");

        // Send the request
        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                return response
                        .getAsJsonObject("data")
                        .getAsJsonArray("translations")
                        .get(0)
                        .getAsJsonObject()
                        .get("translatedText")
                        .getAsString();
               
            }
        } else {
            // Handle error response
            String errorResponse;
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                errorResponse = errorReader.lines().reduce("", (acc, line) -> acc + line);
            }
            throw new Exception("Translation API Error: HTTP " + responseCode + " - " + connection.getResponseMessage()
                    + " - " + errorResponse);
        }
    }
}