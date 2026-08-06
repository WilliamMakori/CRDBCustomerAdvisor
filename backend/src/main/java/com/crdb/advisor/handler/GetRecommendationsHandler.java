package com.crdb.advisor.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crdb.advisor.model.ProductRecommendation;
import com.crdb.advisor.repository.CustomerProfileRepository;
import com.crdb.advisor.repository.RecommendationRepository;
import com.crdb.advisor.service.GeminiService;
import com.crdb.advisor.service.RecommendationService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class GetRecommendationsHandler implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final RecommendationService recommendationService;
    private final ObjectMapper objectMapper;

    public GetRecommendationsHandler() {
        String dynamoEndpoint = System.getenv("DYNAMODB_ENDPOINT");
        DynamoDbClient dynamoDbClient = dynamoEndpoint != null
            ? DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoEndpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("local", "local")))
                .build()
            : DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String geminiApiKey = System.getenv("GEMINI_API_KEY");
        CustomerProfileRepository customerProfileRepository =
            new CustomerProfileRepository(dynamoDbClient);
        RecommendationRepository recommendationRepository =
            new RecommendationRepository(dynamoDbClient);
        GeminiService geminiService = new GeminiService(geminiApiKey);

        this.recommendationService = new RecommendationService(
            geminiService, recommendationRepository, customerProfileRepository);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent event, Context context) {

        context.getLogger().log("GetRecommendations called");

        try {
            String customerId = event.getPathParameters().get("customerId");
            if (customerId == null || customerId.isEmpty()) {
                return buildResponse(400, "{\"error\": \"customerId is required\"}");
            }

            ProductRecommendation recommendation =
                recommendationService.getRecommendations(customerId);
            String responseBody = objectMapper.writeValueAsString(recommendation);
            return buildResponse(200, responseBody);

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            if (e.getMessage() != null &&
                    e.getMessage().contains("profile is incomplete")) {
                return buildResponse(400,
                    "{\"error\": \"" + e.getMessage() + "\"}");
            }
            return buildResponse(500,
                "{\"error\": \"Internal server error: " + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent buildResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(statusCode)
            .withHeaders(headers)
            .withBody(body);
    }
}
