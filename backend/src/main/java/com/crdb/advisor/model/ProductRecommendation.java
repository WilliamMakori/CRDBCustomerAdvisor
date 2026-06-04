package com.crdb.advisor.model;

// we need the list data structure so import List and get started

import java.util.List; 
// what do we need to have here, just like the first project
// when the recommendation was created
// who its tied to
// when it expires
// what it entails
// the LLM used to generate the recommendation 
// there will be a class inside this class that represents what a recommendation is comprised of 

public class ProductRecommendation {

    private String customerId; 
    private String generatedAt;
    private String expiresAt; 
    private String modelUsed; 
    private List<RecommendedProduct> products; 

    // getters and setters 
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public List<RecommendedProduct> getProducts() { return products; }
    public void setProducts(List<RecommendedProduct> products) { this.products = products; }

    



    // Factory method, accessibility, return type and function name
    public static ProductRecommendation createNew(String customerId,
        List<RecommendedProduct> products) {

        ProductRecommendation rec = new ProductRecommendation(); 
        rec.setCustomerId(customerId);
        rec.setProducts(products);
        rec.setGeneratedAt(java.time.Instant.now().toString());
        rec.setExpiresAt(java.time.Instant.now().plusSeconds(86400).toString());
        rec.setModelUsed("amazon.titan-text-express-v1");
        return rec; 

    }
    // what's needed here
    // confidence score
    // reason
    // name of recommendation
    // category of the recommendation ie capital market investment, short term investment long term etc
    // action to take 
    public class RecommendedProduct{

        private String productName; 
        private String category; 
        private String reason; 
        private String actionToTake; 
        private double confidenceScore; 
        private String eligibilityNote; 

        // getters and setters 

        public String getProductName() {return productName;}
        public void setProductName(String productName) { this.productName = productName; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public String getEligibilityNote() { return eligibilityNote; }
        public void setEligibilityNote(String eligibilityNote) { this.eligibilityNote = eligibilityNote; }

        public String getActionToTake() { return actionToTake; }
        public void setActionToTake(String actionToTake) { this.actionToTake = actionToTake; }

        public double getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    
    }
}
