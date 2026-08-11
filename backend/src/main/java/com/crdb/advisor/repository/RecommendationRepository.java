package com.crdb.advisor.repository;

import com.crdb.advisor.model.ProductRecommendation;
import com.crdb.advisor.model.ProductRecommendation.RecommendedProduct;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationRepository {

    private final DynamoDbClient dynamoDbClient;
    private static final String TABLE_NAME = "recommendations";

    public RecommendationRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    // Save a product recommendation to DynamoDB
    public void save(ProductRecommendation recommendation) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("customerId", AttributeValue.builder()
            .s(recommendation.getCustomerId()).build());
        item.put("generatedAt", AttributeValue.builder()
            .s(recommendation.getGeneratedAt()).build());
        item.put("expiresAt", AttributeValue.builder()
            .s(recommendation.getExpiresAt()).build());
        item.put("modelUsed", AttributeValue.builder()
            .s(recommendation.getModelUsed()).build());

        // Convert each RecommendedProduct into a DynamoDB map
        List<AttributeValue> productList = new ArrayList<>();
        for (RecommendedProduct product : recommendation.getProducts()) {
            Map<String, AttributeValue> productMap = new HashMap<>();

            productMap.put("productName", AttributeValue.builder()
                .s(product.getProductName()).build());
            productMap.put("category", AttributeValue.builder()
                .s(product.getCategory()).build());
            productMap.put("reason", AttributeValue.builder()
                .s(product.getReason()).build());
            productMap.put("confidenceScore", AttributeValue.builder()
                .n(String.valueOf(product.getConfidenceScore())).build());

            if (product.getEligibilityNote() != null) {
                productMap.put("eligibilityNote", AttributeValue.builder()
                    .s(product.getEligibilityNote()).build());
            }

            if (product.getActionToTake() != null) {
                productMap.put("actionToTake", AttributeValue.builder()
                    .s(product.getActionToTake()).build());
            }

            productList.add(AttributeValue.builder().m(productMap).build());
        }

        item.put("products", AttributeValue.builder()
            .l(productList).build());

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .build();

        dynamoDbClient.putItem(request);
    }

    // Get the latest recommendation for a customer
    public ProductRecommendation findLatestByCustomerId(String customerId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .keyConditionExpression("customerId = :cid")
            .expressionAttributeValues(Map.of(
                ":cid", AttributeValue.builder().s(customerId).build()
            ))
            .scanIndexForward(false)
            .limit(1)
            .build();

        QueryResponse response = dynamoDbClient.query(request);

        if (response.items().isEmpty()) {
            return null;
        }

        Map<String, AttributeValue> item = response.items().get(0);
        ProductRecommendation recommendation = new ProductRecommendation();
        recommendation.setCustomerId(item.get("customerId").s());
        recommendation.setGeneratedAt(item.get("generatedAt").s());
        recommendation.setExpiresAt(item.get("expiresAt").s());
        recommendation.setModelUsed(item.get("modelUsed").s());

        // Convert DynamoDB maps back into RecommendedProduct objects
        List<RecommendedProduct> products = new ArrayList<>();
        for (AttributeValue av : item.get("products").l()) {
            Map<String, AttributeValue> productMap = av.m();
            RecommendedProduct product = new RecommendedProduct();

            product.setProductName(productMap.get("productName").s());
            product.setCategory(productMap.get("category").s());
            product.setReason(productMap.get("reason").s());
            product.setConfidenceScore(
                Double.parseDouble(productMap.get("confidenceScore").n())
            );

            if (productMap.containsKey("eligibilityNote")) {
                product.setEligibilityNote(productMap.get("eligibilityNote").s());
            }

            if (productMap.containsKey("actionToTake")) {
                product.setActionToTake(productMap.get("actionToTake").s());
            }

            products.add(product);
        }

        recommendation.setProducts(products);
        return recommendation;
    }

    // Check if a fresh recommendation exists for a customer
    public boolean hasFreshRecommendation(String customerId) {
        ProductRecommendation latest = findLatestByCustomerId(customerId);
        if (latest == null) {
            return false;
        }
        java.time.Instant expiry = java.time.Instant.parse(latest.getExpiresAt());
        return expiry.isAfter(java.time.Instant.now());
    }

    // Delete all recommendations for a customer
public void deleteByCustomerId(String customerId) {
    // Find the latest recommendation
    ProductRecommendation latest = findLatestByCustomerId(customerId);
    if (latest == null) {
        return;
    }

    Map<String, AttributeValue> key = new HashMap<>();
    key.put("customerId", AttributeValue.builder()
        .s(customerId).build());
    key.put("generatedAt", AttributeValue.builder()
        .s(latest.getGeneratedAt()).build());

    DeleteItemRequest request = DeleteItemRequest.builder()
        .tableName(TABLE_NAME)
        .key(key)
        .build();

    dynamoDbClient.deleteItem(request);
}
}