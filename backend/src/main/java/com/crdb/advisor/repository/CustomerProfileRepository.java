package com.crdb.advisor.repository;

import com.crdb.advisor.model.CustomerProfile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerProfileRepository {

    private final DynamoDbClient dynamoDbClient;
    private static final String TABLE_NAME = "customer-profiles";

    public CustomerProfileRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    // Save a customer profile to DynamoDB
    public void save(CustomerProfile profile) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("customerId", AttributeValue.builder()
            .s(profile.getCustomerId()).build());
        item.put("fullName", AttributeValue.builder()
            .s(profile.getFullName()).build());
        item.put("email", AttributeValue.builder()
            .s(profile.getEmail()).build());
        item.put("age", AttributeValue.builder()
            .n(String.valueOf(profile.getAge())).build());
        item.put("createdAt", AttributeValue.builder()
            .s(profile.getCreatedAt()).build());
        item.put("updatedAt", AttributeValue.builder()
            .s(profile.getUpdatedAt()).build());

        if (profile.getEmploymentStatus() != null) {
            item.put("employmentStatus", AttributeValue.builder()
                .s(profile.getEmploymentStatus()).build());
        }

        if (profile.getMonthlyIncomeRange() != null) {
            item.put("monthlyIncomeRange", AttributeValue.builder()
                .s(profile.getMonthlyIncomeRange()).build());
        }

        if (profile.getRiskAppetite() != null) {
            item.put("riskAppetite", AttributeValue.builder()
                .s(profile.getRiskAppetite()).build());
        }

        if (profile.getInvestmentHorizon() != null) {
            item.put("investmentHorizon", AttributeValue.builder()
                .s(profile.getInvestmentHorizon()).build());
        }

        if (profile.getAvailableCapital() > 0) {
            item.put("availableCapital", AttributeValue.builder()
                .n(String.valueOf(profile.getAvailableCapital())).build());
        }

        if (profile.getInvestmentGoals() != null && !profile.getInvestmentGoals().isEmpty()) {
            item.put("investmentGoals", AttributeValue.builder()
                .ss(profile.getInvestmentGoals()).build());
        }

        if (profile.getExistingProducts() != null && !profile.getExistingProducts().isEmpty()) {
            item.put("existingProducts", AttributeValue.builder()
                .ss(profile.getExistingProducts()).build());
        }

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .build();

        dynamoDbClient.putItem(request);
    }

    // Get a customer profile from DynamoDB by customerId
    public CustomerProfile findById(String customerId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("customerId", AttributeValue.builder()
            .s(customerId).build());

        GetItemRequest request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .build();

        GetItemResponse response = dynamoDbClient.getItem(request);

        if (!response.hasItem()) {
            return null;
        }

        Map<String, AttributeValue> item = response.item();
        CustomerProfile profile = new CustomerProfile();

        profile.setCustomerId(item.get("customerId").s());
        profile.setFullName(item.get("fullName").s());
        profile.setEmail(item.get("email").s());
        profile.setAge(Integer.parseInt(item.get("age").n()));
        profile.setCreatedAt(item.get("createdAt").s());
        profile.setUpdatedAt(item.get("updatedAt").s());

        if (item.containsKey("employmentStatus")) {
            profile.setEmploymentStatus(item.get("employmentStatus").s());
        }

        if (item.containsKey("monthlyIncomeRange")) {
            profile.setMonthlyIncomeRange(item.get("monthlyIncomeRange").s());
        }

        if (item.containsKey("riskAppetite")) {
            profile.setRiskAppetite(item.get("riskAppetite").s());
        }

        if (item.containsKey("investmentHorizon")) {
            profile.setInvestmentHorizon(item.get("investmentHorizon").s());
        }

        if (item.containsKey("availableCapital")) {
            profile.setAvailableCapital(
                Double.parseDouble(item.get("availableCapital").n())
            );
        }

        if (item.containsKey("investmentGoals")) {
            profile.setInvestmentGoals(
                new ArrayList<>(item.get("investmentGoals").ss())
            );
        }

        if (item.containsKey("existingProducts")) {
            profile.setExistingProducts(
                new ArrayList<>(item.get("existingProducts").ss())
            );
        }

        return profile;
    }

    // Delete a customer profile from DynamoDB
    public void delete(String customerId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("customerId", AttributeValue.builder()
            .s(customerId).build());

        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .build();

        dynamoDbClient.deleteItem(request);
    }
}