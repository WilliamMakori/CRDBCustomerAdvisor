package com.crdb.advisor.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crdb.advisor.model.CustomerProfile;
import com.crdb.advisor.repository.CustomerProfileRepository;
import com.crdb.advisor.repository.RecommendationRepository;
import com.crdb.advisor.service.BedrockService;
import com.crdb.advisor.service.RecommendationService;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;

public class GetProfileHandler implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final RecommendationService recommendationService;
    private final ObjectMapper objectMapper;

    public GetProfileHandler() {
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

        context.getLogger().log("GetProfile called");

        try {
            // Step 1 — extract customerId from the URL
            String customerId = event.getPathParameters().get("customerId");
            if (customerId == null || customerId.isEmpty()) {
                return buildResponse(400,
                    "{\"error\": \"customerId is required\"}");
            }

            // Step 2 — fetch the customer profile
            CustomerProfile profile = recommendationService
                .getCustomerProfile(customerId);

            // Step 3 — convert to JSON and return
            String responseBody = objectMapper.writeValueAsString(profile);
            return buildResponse(200, responseBody);

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());

            if (e.getMessage() != null &&
                    e.getMessage().contains("not found")) {
                return buildResponse(404,
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