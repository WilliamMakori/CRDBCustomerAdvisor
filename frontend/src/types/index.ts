// Represents a single CRDB product recommendation
export interface RecommendedProduct {
    productName: string;
    category: string;
    reason: string;
    eligibilityNote: string;
    actionToTake: string;
    confidenceScore: number;
}

// Represents a full batch of recommendations for a customer
export interface ProductRecommendation {
    customerId: string;
    generatedAt: string;
    expiresAt: string;
    modelUsed: string;
    products: RecommendedProduct[];
}

// Represents a CRDB customer profile
export interface CustomerProfile {
    customerId: string;
    fullName: string;
    email: string;
    age: number;
    employmentStatus: string;
    monthlyIncomeRange: string;
    riskAppetite: string;
    investmentGoals: string[];
    existingProducts: string[];
    investmentHorizon: string;
    availableCapital: number;
    createdAt: string;
    updatedAt: string;
}

// Response from update profile API
export interface UpdateProfileResponse {
    message: string;
}

// Employment status options
export const EMPLOYMENT_OPTIONS: string[] = [
    "Salaried",
    "Self-employed",
    "Business Owner",
    "Retired",
    "Student",
    "Unemployed"
];

// Monthly income ranges in TZS
export const INCOME_RANGES: string[] = [
    "Below 500,000",
    "500,000 - 1,000,000",
    "1,000,000 - 3,000,000",
    "3,000,000 - 5,000,000",
    "5,000,000 - 10,000,000",
    "Above 10,000,000"
];

// Risk appetite options
export const RISK_OPTIONS: string[] = [
    "Low",
    "Medium",
    "High"
];

// Investment horizon options
export const HORIZON_OPTIONS: string[] = [
    "Short-term (less than 1 year)",
    "Medium-term (1 to 5 years)",
    "Long-term (more than 5 years)"
];

// Investment goals options
export const INVESTMENT_GOALS: string[] = [
    "Retirement Planning",
    "Wealth Growth",
    "Capital Preservation",
    "Education Funding",
    "Home Purchase",
    "Emergency Fund",
    "Business Investment",
    "Income Generation"
];

// Existing CRDB products a customer might already hold
export const CRDB_PRODUCTS: string[] = [
    "Savings Account",
    "Current Account",
    "Fixed Deposit",
    "Personal Loan",
    "Mortgage",
    "Government Securities",
    "Collective Investment Schemes",
    "Securities Trading Account",
    "Medical Insurance",
    "Personal Accident Policy"
];

// Product category colors for the dashboard
export const CATEGORY_COLORS: Record<string, string> = {
    "Capital Markets": "#1a7a4a",
    "Personal Banking": "#1a4a7a",
    "Insurance": "#7a4a1a"
};