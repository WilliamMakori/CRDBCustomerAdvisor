package com.crdb.advisor.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crdb.advisor.model.CustomerProfile;
import com.crdb.advisor.repository.CustomerProfileRepository;
import com.crdb.advisor.repository.RecommendationRepository;
import com.crdb.advisor.service.BedrockService;
import com.crdb.advisor.service.RecommendationService;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProfileHandler implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final RecommendationService recommendationService;
    private final ObjectMapper objectMapper;

    public UpdateProfileHandler() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
        BedrockRuntimeClient bedrockClient = BedrockRuntimeClient.builder().build();

        CustomerProfileRepository customerProfileRepository =
            new CustomerProfileRepository(dynamoDbClient);
        RecommendationRepository recommendationRepository =
            new RecommendationRepository(dynamoDbClient);
        BedrockService bedrockService =
            new BedrockService(bedrockClient);

        this.recommendationService = new RecommendationService(
            bedrockService,
            recommendationRepository,
            customerProfileRepository
        );

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent event,
            Context context) {

        context.getLogger().log("UpdateProfile called");

        try {
            // Step 1 — extract customerId from the URL
            String customerId = event.getPathParameters().get("customerId");
            if (customerId == null || customerId.isEmpty()) {
                return buildResponse(400,
                    "{\"error\": \"customerId is required\"}");
            }

            // Step 2 — parse the request body
            String body = event.getBody();
            if (body == null || body.isEmpty()) {
                return buildResponse(400,
                    "{\"error\": \"Request body is required\"}");
            }

            JsonNode bodyNode = objectMapper.readTree(body);
            CustomerProfile updatedProfile = new CustomerProfile();

            // Step 3 — extract each field if present
            if (bodyNode.has("employmentStatus")) {
                updatedProfile.setEmploymentStatus(
                    bodyNode.get("employmentStatus").asText());
            }
            if (bodyNode.has("monthlyIncomeRange")) {
                updatedProfile.setMonthlyIncomeRange(
                    bodyNode.get("monthlyIncomeRange").asText());
            }
            if (bodyNode.has("riskAppetite")) {
                updatedProfile.setRiskAppetite(
                    bodyNode.get("riskAppetite").asText());
            }
            if (bodyNode.has("investmentHorizon")) {
                updatedProfile.setInvestmentHorizon(
                    bodyNode.get("investmentHorizon").asText());
            }
            if (bodyNode.has("availableCapital")) {
                updatedProfile.setAvailableCapital(
                    bodyNode.get("availableCapital").asDouble());
            }
            if (bodyNode.has("age")) {
                updatedProfile.setAge(
                    bodyNode.get("age").asInt());
            }
            if (bodyNode.has("investmentGoals")) {
                List<String> goals = new ArrayList<>();
                for (JsonNode goal : bodyNode.get("investmentGoals")) {
                    goals.add(goal.asText());
                }
                updatedProfile.setInvestmentGoals(goals);
            }
            if (bodyNode.has("existingProducts")) {
                List<String> products = new ArrayList<>();
                for (JsonNode product : bodyNode.get("existingProducts")) {
                    products.add(product.asText());
                }
                updatedProfile.setExistingProducts(products);
            }

            // Step 4 — save the updated profile
            recommendationService.updateProfile(customerId, updatedProfile);

            return buildResponse(200,
                "{\"message\": \"Profile updated successfully\"}");

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return buildResponse(500,
                "{\"error\": \"Internal server error: "
                    + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent buildResponse(
            int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");

        return new APIGatewayProxyResponseEvent()
            .withStatusCode(statusCode)
            .withHeaders(headers)
            .withBody(body);
    }
}