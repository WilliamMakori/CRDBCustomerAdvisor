package com.crdb.advisor.service;

import com.crdb.advisor.model.CustomerProfile;
import com.crdb.advisor.model.ProductRecommendation;
import com.crdb.advisor.model.ProductRecommendation.RecommendedProduct;
import com.crdb.advisor.repository.CustomerProfileRepository;
import com.crdb.advisor.repository.RecommendationRepository;

import java.util.List;

public class RecommendationService {

    private final GeminiService geminiService;
    private final RecommendationRepository recommendationRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public RecommendationService(
            GeminiService geminiService,
            RecommendationRepository recommendationRepository,
            CustomerProfileRepository customerProfileRepository) {
        this.geminiService = geminiService;
        this.recommendationRepository = recommendationRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    public ProductRecommendation getRecommendations(String customerId) throws Exception {
        if (recommendationRepository.hasFreshRecommendation(customerId)) {
            return recommendationRepository.findLatestByCustomerId(customerId);
        }

        CustomerProfile profile = customerProfileRepository.findById(customerId);
        if (profile == null) {
            throw new Exception("Customer not found: " + customerId);
        }

        if (!isProfileComplete(profile)) {
            throw new Exception("Customer profile is incomplete. " +
                "Please fill in employment status, income range, " +
                "risk appetite and investment goals before requesting recommendations.");
        }

        List<RecommendedProduct> products = geminiService
            .generateRecommendations(profile);

        ProductRecommendation recommendation = ProductRecommendation
            .createNew(customerId, products);
        recommendationRepository.save(recommendation);

        return recommendation;
    }

    public void updateProfile(String customerId, CustomerProfile updatedProfile)
            throws Exception {
        CustomerProfile existing = customerProfileRepository.findById(customerId);
        if (existing == null) {
            throw new Exception("Customer not found: " + customerId);
        }

        if (updatedProfile.getEmploymentStatus() != null)
            existing.setEmploymentStatus(updatedProfile.getEmploymentStatus());
        if (updatedProfile.getMonthlyIncomeRange() != null)
            existing.setMonthlyIncomeRange(updatedProfile.getMonthlyIncomeRange());
        if (updatedProfile.getRiskAppetite() != null)
            existing.setRiskAppetite(updatedProfile.getRiskAppetite());
        if (updatedProfile.getInvestmentHorizon() != null)
            existing.setInvestmentHorizon(updatedProfile.getInvestmentHorizon());
        if (updatedProfile.getInvestmentGoals() != null)
            existing.setInvestmentGoals(updatedProfile.getInvestmentGoals());
        if (updatedProfile.getExistingProducts() != null)
            existing.setExistingProducts(updatedProfile.getExistingProducts());
        if (updatedProfile.getAvailableCapital() > 0)
            existing.setAvailableCapital(updatedProfile.getAvailableCapital());
        if (updatedProfile.getAge() > 0)
            existing.setAge(updatedProfile.getAge());

        existing.setUpdatedAt(java.time.Instant.now().toString());
        customerProfileRepository.save(existing);
    }

    public CustomerProfile createCustomer(
            String customerId, String fullName, String email, int age) {
        CustomerProfile profile = CustomerProfile.createNew(
            customerId, fullName, email, age);
        customerProfileRepository.save(profile);
        return profile;
    }

    public CustomerProfile getCustomerProfile(String customerId) throws Exception {
        CustomerProfile profile = customerProfileRepository.findById(customerId);
        if (profile == null) {
            throw new Exception("Customer not found: " + customerId);
        }
        return profile;
    }

    private boolean isProfileComplete(CustomerProfile profile) {
        return profile.getEmploymentStatus() != null
            && profile.getMonthlyIncomeRange() != null
            && profile.getRiskAppetite() != null
            && profile.getInvestmentGoals() != null
            && !profile.getInvestmentGoals().isEmpty();
    }
}
