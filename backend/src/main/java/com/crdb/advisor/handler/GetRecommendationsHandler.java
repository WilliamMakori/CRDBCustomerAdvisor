package com.crdb.advisor.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crdb.advisor.model.ProductRecommendation;
import com.crdb.advisor.repository.CustomerProfileRepository;
import com.crdb.advisor.repository.RecommendationRepository;
import com.crdb.advisor.service.BedrockService;
import com.crdb.advisor.service.RecommendationService;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;

public class GetRecommendationsHandler implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final RecommendationService recommendationService;
    private final ObjectMapper objectMapper;

    public GetRecommendationsHandler() {
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

        context.getLogger().log("GetRecommendations called for: "
            + event.getPathParameters());

        try {
            // Step 1 — extract customerId from the URL
            String customerId = event.getPathParameters().get("customerId");
            if (customerId == null || customerId.isEmpty()) {
                return buildResponse(400,
                    "{\"error\": \"customerId is required\"}");
            }

            // Step 2 — get recommendations from the service
            ProductRecommendation recommendation = recommendationService
                .getRecommendations(customerId);

            // Step 3 — convert to JSON and return
            String responseBody = objectMapper
                .writeValueAsString(recommendation);
            return buildResponse(200, responseBody);

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());

            // Return a specific message if profile is incomplete
            if (e.getMessage() != null &&
                    e.getMessage().contains("profile is incomplete")) {
                return buildResponse(400,
                    "{\"error\": \"" + e.getMessage() + "\"}");
            }

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