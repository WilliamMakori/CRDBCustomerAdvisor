package com.crdb.advisor.model; 

import java.util.List; 

public class CustomerProfile{

    // for this class create a variable, getters and setters for each piece of information that a user should have

    private String customerId; 
    private String fullName; 
    private String email; 
    private int age; 
    private String employmentStatus; 
    private String monthlyIncomeRange; 
    private String riskAppetite; 
    private List<String> investmentGoals; 
    private List<String> existingProducts; 
    private String investmentHorizon; 
    private double availableCapital; 
    private String createdAt; 
    private String updatedAt; 

    // Getters and Setters for each of the variables created
    public String getCustomerId() {return customerId;}
    public void setCustomerId(String customerId) {this.customerId = customerId;}
    
    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}

    public String getEmail() {return email;}
    public void setEmail(String email){this.email = email;}

    public int getAge() {return age;}
    public void setAge(int age) {this.age = age;}

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getMonthlyIncomeRange() { return monthlyIncomeRange; }
    public void setMonthlyIncomeRange(String monthlyIncomeRange) { this.monthlyIncomeRange = monthlyIncomeRange; }

    public String getRiskAppetite() { return riskAppetite; }
    public void setRiskAppetite(String riskAppetite) { this.riskAppetite = riskAppetite; }

    public List<String> getInvestmentGoals() { return investmentGoals; }
    public void setInvestmentGoals(List<String> investmentGoals) { this.investmentGoals = investmentGoals; }

    public List<String> getExistingProducts() { return existingProducts; }
    public void setExistingProducts(List<String> existingProducts) { this.existingProducts = existingProducts; }

    public String getInvestmentHorizon() { return investmentHorizon; }
    public void setInvestmentHorizon(String investmentHorizon) { this.investmentHorizon = investmentHorizon; }

    public double getAvailableCapital() { return availableCapital; }
    public void setAvailableCapital(double availableCapital) { this.availableCapital = availableCapital; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Factory method with data to initialize a new customer profile
    public static CustomerProfile createNew(
        String customerId,
        String fullName,
        String email, 
        int age){

            CustomerProfile profile = new CustomerProfile(); 
            profile.customerId = customerId; 
            profile.age = age; 
            profile.email = email; 
            profile.fullName = fullName; 
            // set the risk appetite, investment horizon, availableCapital and the date when the profile was created and updated
            profile.setRiskAppetite("Medium");
            profile.setInvestmentHorizon("Medium-term");
            profile.setAvailableCapital(0);
            profile.setCreatedAt(java.time.Instant.now().toString()); 
            profile.setUpdatedAt(java.time.Instant.now().toString());
            // when do we change the investment goals and the employment status
            // also how about the income range 
            return profile; 
        }

}
