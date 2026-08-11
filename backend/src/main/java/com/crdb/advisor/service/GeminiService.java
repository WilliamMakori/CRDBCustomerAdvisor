package com.crdb.advisor.service;

import com.crdb.advisor.model.CustomerProfile;
import com.crdb.advisor.model.ProductRecommendation.RecommendedProduct;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GeminiService {

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
   private static final String GEMINI_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/" +
    "gemini-3.5-flash:generateContent?key=";

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    private String buildPrompt(CustomerProfile profile) {
    StringBuilder prompt = new StringBuilder();

    prompt.append("You are a CRDB Bank Tanzania financial advisor. ");
    prompt.append("Respond with ONLY a JSON object, no other text. ");
    prompt.append("Format: {\"recommendations\":[{\"productName\":\"\",\"category\":\"\",\"reason\":\"\",\"eligibilityNote\":\"\",\"actionToTake\":\"\",\"confidenceScore\":0.0}]} ");
    prompt.append("Recommend exactly 5 CRDB products for this customer: ");
    prompt.append("Age:").append(profile.getAge()).append(" ");
    prompt.append("Employment:").append(profile.getEmploymentStatus()).append(" ");
    prompt.append("Income:").append(profile.getMonthlyIncomeRange()).append(" TZS ");
    prompt.append("Risk:").append(profile.getRiskAppetite()).append(" ");
    prompt.append("Horizon:").append(profile.getInvestmentHorizon()).append(" ");
    prompt.append("Capital:").append(profile.getAvailableCapital()).append(" TZS ");

    if (profile.getInvestmentGoals() != null && !profile.getInvestmentGoals().isEmpty()) {
        prompt.append("Goals:").append(String.join(",", profile.getInvestmentGoals())).append(" ");
    }

    if (profile.getExistingProducts() != null && !profile.getExistingProducts().isEmpty()) {
        prompt.append("Already has:").append(String.join(",", profile.getExistingProducts())).append(" ");
    }

    prompt.append("Available products: ");
    prompt.append("1.Government Securities(low risk,Capital Markets) ");
    prompt.append("2.Collective Investment Schemes(medium risk,Capital Markets) ");
    prompt.append("3.Securities Trading(high risk,Capital Markets) ");
    prompt.append("4.IPO Management(high risk,Capital Markets) ");
    prompt.append("5.Retirement Planning Staafu Kibabe(low risk,Capital Markets) ");
    prompt.append("6.Financial Doctor min TZS 50M(low risk,Capital Markets) ");
    prompt.append("7.Investment Management min TZS 250M(medium risk,Capital Markets) ");
    prompt.append("8.Research and Analysis(low risk,Capital Markets) ");
    prompt.append("9.Savings Account(low risk,Personal Banking) ");
    prompt.append("10.Fixed Deposit Thamani Account(low risk,Personal Banking) ");
    prompt.append("11.Dhahabu Account(low risk,Personal Banking) ");
    prompt.append("12.Personal Loan(medium risk,Personal Banking) ");
    prompt.append("13.Jijenge Mortgage min TZS 20M(medium risk,Personal Banking) ");
    prompt.append("14.Medical Insurance(low risk,Insurance) ");
    prompt.append("15.Personal Accident Policy(low risk,Insurance) ");
    prompt.append("Match risk appetite strictly. Only recommend affordable products. JSON only.");

    return prompt.toString();
}

    public List<RecommendedProduct> generateRecommendations(
            CustomerProfile profile) throws Exception {

        String prompt = buildPrompt(profile);

        String requestBody = objectMapper.writeValueAsString(
    new java.util.HashMap<String, Object>() {{
        put("contents", new Object[]{
            new java.util.HashMap<String, Object>() {{
                put("parts", new Object[]{
                    new java.util.HashMap<String, Object>() {{
                        put("text", prompt);
                    }}
                });
            }}
        });
        put("generationConfig", new java.util.HashMap<String, Object>() {{
            put("temperature", 0.3);
            put("maxOutputTokens", 4096);
            put("responseMimeType", "application/json");
        }});
    }}
);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(GEMINI_URL + apiKey))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(
            request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Gemini API error: " + response.statusCode()
                + " " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String outputText = root
            .path("candidates")
            .get(0)
            .path("content")
            .path("parts")
            .get(0)
            .path("text")
            .asText();

        outputText = outputText
    .replace("```json", "")
    .replace("```", "")
    .trim();
    // Fix missing closing brace if Gemini truncated it
if (outputText.endsWith("]") || outputText.endsWith("]\n")) {
    outputText = outputText + "}";
}
if (!outputText.endsWith("}")) {
    outputText = outputText + "]}";
}
    System.out.println("GEMINI RESPONSE: " + outputText);

        JsonNode recommendationsNode = objectMapper
            .readTree(outputText)
            .path("recommendations");

        List<RecommendedProduct> products = new ArrayList<>();
        for (JsonNode node : recommendationsNode) {
            RecommendedProduct product = new RecommendedProduct();
            product.setProductName(node.path("productName").asText());
            product.setCategory(node.path("category").asText());
            product.setReason(node.path("reason").asText());
            product.setEligibilityNote(node.path("eligibilityNote").asText());
            product.setActionToTake(node.path("actionToTake").asText());
            product.setConfidenceScore(node.path("confidenceScore").asDouble());
            products.add(product);
        }

        return products;
    }
}
