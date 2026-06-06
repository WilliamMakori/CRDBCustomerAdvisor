package com.crdb.advisor.service;

import com.crdb.advisor.model.CustomerProfile;
import com.crdb.advisor.model.ProductRecommendation;
import com.crdb.advisor.model.ProductRecommendation.RecommendedProduct;
import com.crdb.advisor.repository.CustomerProfileRepository;
import com.crdb.advisor.repository.RecommendationRepository;

import java.util.List;

public class RecommendationService {

    private final BedrockService bedrockService;
    private final RecommendationRepository recommendationRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public RecommendationService(
            BedrockService bedrockService,
            RecommendationRepository recommendationRepository,
            CustomerProfileRepository customerProfileRepository) {
        this.bedrockService = bedrockService;
        this.recommendationRepository = recommendationRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    // Main method — gets product recommendations for a customer
    public ProductRecommendation getRecommendations(String customerId) throws Exception {

        // Step 1 — check if fresh recommendations already exist
        if (recommendationRepository.hasFreshRecommendation(customerId)) {
            return recommendationRepository.findLatestByCustomerId(customerId);
        }

        // Step 2 — fetch the customer profile
        CustomerProfile profile = customerProfileRepository.findById(customerId);
        if (profile == null) {
            throw new Exception("Customer not found: " + customerId);
        }

        // Step 3 — check if profile is complete enough for recommendations
        if (!isProfileComplete(profile)) {
            throw new Exception("Customer profile is incomplete. " +
                "Please fill in employment status, income range, " +
                "risk appetite and investment goals before requesting recommendations.");
        }

        // Step 4 — call Bedrock to generate fresh recommendations
        List<RecommendedProduct> products = bedrockService
            .generateRecommendations(profile);

        // Step 5 — wrap in ProductRecommendation and save to DynamoDB
        ProductRecommendation recommendation = ProductRecommendation
            .createNew(customerId, products);
        recommendationRepository.save(recommendation);

        return recommendation;
    }

    // Update a customer's financial profile
    public void updateProfile(String customerId, CustomerProfile updatedProfile)
            throws Exception {
        CustomerProfile existing = customerProfileRepository.findById(customerId);
        if (existing == null) {
            throw new Exception("Customer not found: " + customerId);
        }

        // Only update fields that were actually provided
        if (updatedProfile.getEmploymentStatus() != null) {
            existing.setEmploymentStatus(updatedProfile.getEmploymentStatus());
        }
        if (updatedProfile.getMonthlyIncomeRange() != null) {
            existing.setMonthlyIncomeRange(updatedProfile.getMonthlyIncomeRange());
        }
        if (updatedProfile.getRiskAppetite() != null) {
            existing.setRiskAppetite(updatedProfile.getRiskAppetite());
        }
        if (updatedProfile.getInvestmentHorizon() != null) {
            existing.setInvestmentHorizon(updatedProfile.getInvestmentHorizon());
        }
        if (updatedProfile.getInvestmentGoals() != null) {
            existing.setInvestmentGoals(updatedProfile.getInvestmentGoals());
        }
        if (updatedProfile.getExistingProducts() != null) {
            existing.setExistingProducts(updatedProfile.getExistingProducts());
        }
        if (updatedProfile.getAvailableCapital() > 0) {
            existing.setAvailableCapital(updatedProfile.getAvailableCapital());
        }
        if (updatedProfile.getAge() > 0) {
            existing.setAge(updatedProfile.getAge());
        }

        existing.setUpdatedAt(java.time.Instant.now().toString());
        customerProfileRepository.save(existing);
    }

    // Create a brand new customer profile
    public CustomerProfile createCustomer(
            String customerId,
            String fullName,
            String email,
            int age) {
        CustomerProfile profile = CustomerProfile.createNew(
            customerId, fullName, email, age);
        customerProfileRepository.save(profile);
        return profile;
    }

    // Get a customer profile
    public CustomerProfile getCustomerProfile(String customerId) throws Exception {
        CustomerProfile profile = customerProfileRepository.findById(customerId);
        if (profile == null) {
            throw new Exception("Customer not found: " + customerId);
        }
        return profile;
    }

    // Check if a profile has enough information for recommendations
    private boolean isProfileComplete(CustomerProfile profile) {
        return profile.getEmploymentStatus() != null
            && profile.getMonthlyIncomeRange() != null
            && profile.getRiskAppetite() != null
            && profile.getInvestmentGoals() != null
            && !profile.getInvestmentGoals().isEmpty();
    }
}