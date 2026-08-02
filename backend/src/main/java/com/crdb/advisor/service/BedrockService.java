package com.crdb.advisor.service;

import com.crdb.advisor.model.CustomerProfile;
import com.crdb.advisor.model.ProductRecommendation.RecommendedProduct;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedrockService {
    // this variable below is the prompt that will be sent to the bedrock model to generate product recommendations based on the customer profile

    private final BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper;
    // name of the bedrock model for text generation
    private static final String MODEL_ID = "amazon.titan-text-express-v1";
    // private static final String MODEL_ID = "amazon.titan-text-v1"; // alternative model 
    // we need an alternative model because the first one is not available in all regions and the second one is more expensive to use? 
    // we can use the free tier model for testing and development, but we should use the paid model for production because it is more reliable and has better performance. 
    
    // the function below calls the bedrock model to generate product recommendations based on the customer profile using the prompt built above
    
    public BedrockService(BedrockRuntimeClient bedrockClient) {
        this.bedrockClient = bedrockClient;
        this.objectMapper = new ObjectMapper();
    }

    // Build a detailed financial advisory prompt from the customer profile
    // we parse in the user who's profile we want to build the recommendation off of. and we return a string that has the JSON data containing the recommendations

    private String buildPrompt(CustomerProfile profile) {
        // the variable below is the prompt sent to the bedrock model to generate recommendations.
        // this variable works by giving the model context about the customer profile and the available products, then asking it to recommend 5 products that match the user's profile

        StringBuilder prompt = new StringBuilder();
        // context for the model to understand the task it's supposed to perform
        // we first tell the AI what it is and what it should do and what rules it should follow
        // prompt engineering


        prompt.append("You are a professional financial advisor at CRDB Bank Tanzania. ");
        prompt.append("Your job is to recommend the most suitable CRDB products to a customer based on their financial profile. ");
        prompt.append("Recommend exactly 5 products. ");
        prompt.append("Only recommend products the customer is eligible for based on their available capital. ");
        prompt.append("Match products to the customer's risk appetite strictly — never recommend high-risk products to low-risk customers. ");
        // telling it the format it should respond in so that we actually parse the response easily and not have to deal with unstructured text 

        prompt.append("Respond in this exact JSON format with no additional text: ");
        prompt.append("{\"recommendations\": [");
        prompt.append("{\"productName\": \"\", \"category\": \"\", \"reason\": \"\", ");
        prompt.append("\"eligibilityNote\": \"\", \"actionToTake\": \"\", \"confidenceScore\": 0.0}");
        prompt.append("]} ");
        // an example of a valid response from the model, one out of 5 is
        // a recommendation that the user takes on a Collective Investment scheme because it matches their risk appetite, goals, income and capital
        // the recommendation would include the category, reason, confidence score and the product name as well as the action to take and an eligibility note that explains why the customer can take on said investment
        // the profile would be one of a young professional that's salaried with a low amount of savings and a low risk appetite, looking to invest long term without putting too much effort into managing their investments
        // the responses provided here will be used to create recommended product objects

        // THESE ARE POINTS TO SPEAK ABOUT IN INTERVIEWS 

        // we then proceed to feed the model with the customer profile AND the available products so that it can make recommendations based on the context we have given it

        // Customer profile details
        prompt.append("CUSTOMER PROFILE: ");
        prompt.append("Age: ").append(profile.getAge()).append(". ");
        prompt.append("Employment: ").append(profile.getEmploymentStatus()).append(". ");
        prompt.append("Monthly income range: ").append(profile.getMonthlyIncomeRange()).append(" TZS. ");
        prompt.append("Risk appetite: ").append(profile.getRiskAppetite()).append(". ");
        prompt.append("Investment horizon: ").append(profile.getInvestmentHorizon()).append(". ");
        prompt.append("Available capital: ").append(profile.getAvailableCapital()).append(" TZS. ");

        // we check if the user has investment goals, and if they don't then that portion in their profile isn't left as null
        // in what format are the investment goals stored in
        // they're stored in the form of a singly linkedlist, not sure what they look like exactly will have to double check this
        // not null meaning its a pointer and not empty meaning it actually has data apart from an initiated pointer value

        if (profile.getInvestmentGoals() != null && !profile.getInvestmentGoals().isEmpty()) {

            prompt.append("Investment goals: ")
                .append(String.join(", ", profile.getInvestmentGoals())).append(". ");
        }
        // check if the user already has some products recommended on their profile and if they do list them out, remember not to recommend products twice


        if (profile.getExistingProducts() != null && !profile.getExistingProducts().isEmpty()) {
            // what happens here? what gets appended?? 

            prompt.append("Products already held: ")
                .append(String.join(", ", profile.getExistingProducts())).append(". ");
            prompt.append("Do not recommend products the customer already holds. ");
        }

        // Full CRDB product catalogue
        // the entire catalogue has to be hard coded in here, there are products that aren't here, you need to be able to explain what the app already does. Do i Have to use amazon bedrock? 
        
        prompt.append("AVAILABLE CRDB PRODUCTS: ");

        prompt.append("CAPITAL MARKETS: ");
        prompt.append("1. Government Securities (Treasury Bills and Treasury Bonds) — ");
        prompt.append("low risk, stable returns, issued by Government of Tanzania. ");
        prompt.append("Treasury Bills are short-term, Treasury Bonds are long-term with semiannual coupons. ");
        prompt.append("Action: Visit any CRDB branch or contact Relationship Manager to open a BOT CDS account. ");

        prompt.append("2. Collective Investment Schemes — ");
        prompt.append("medium risk, pooled investment funds across shares bonds and fixed deposits. ");
        prompt.append("Benefits include diversification professional management and tax advantages. ");
        prompt.append("Action: Choose a CIS that matches goals and risk appetite, complete KYC documents. ");

        prompt.append("3. Securities Trading — ");
        prompt.append("high risk, buy and sell equities and bonds on the Dar es Salaam Stock Exchange. ");
        prompt.append("Action: Open a Trading Account with CRDB, fund it and start trading. ");

        prompt.append("4. IPO Management — ");
        prompt.append("medium to high risk, participate in new public offerings of shares and bonds. ");
        prompt.append("Track record of 30+ successful IPOs. ");
        prompt.append("Action: Contact CRDB Capital Markets team for investor applications. ");

        prompt.append("5. Retirement Planning (Staafu Kibabe) — ");
        prompt.append("low risk, long-term retirement cash flow and financial stability. ");
        prompt.append("Eligibility: 18 years or older planning to retire. ");
        prompt.append("Action: Complete Wealth Management Application Form and valid ID. ");

        prompt.append("6. Financial Doctor — ");
        prompt.append("one-on-one financial advisory and portfolio assessment for new investors. ");
        prompt.append("Minimum investment: TZS 50,000,000. ");
        prompt.append("Action: Complete Wealth Management Application Form, minimum TZS 50M required. ");

        prompt.append("7. Investment Management — ");
        prompt.append("professional discretionary portfolio management for high net worth individuals. ");
        prompt.append("Minimum investment: TZS 250,000,000. ");
        prompt.append("Action: Complete Wealth Management Application Form, minimum TZS 250M required. ");

        prompt.append("8. Research and Analysis — ");
        prompt.append("market intelligence reports, economic outlook, DSE-focused insights. ");
        prompt.append("Action: Share information needs with CRDB Capital Markets team. ");

        prompt.append("PERSONAL BANKING: ");
        prompt.append("9. Savings Account — low risk, save for future needs with interest. ");
        prompt.append("Action: Visit any CRDB branch to open an account. ");

        prompt.append("10. Fixed Deposit (Thamani Account) — ");
        prompt.append("low risk, deposit funds for a fixed period at guaranteed interest rate. ");
        prompt.append("Action: Visit any CRDB branch to open a fixed deposit account. ");

        prompt.append("11. Dhahabu Account — ");
        prompt.append("low risk, regular monthly savings with fixed contribution amounts. ");
        prompt.append("Action: Visit any CRDB branch to open a Dhahabu account. ");

        prompt.append("12. Personal Loan — ");
        prompt.append("medium risk, loan facility for personal growth and development. ");
        prompt.append("Action: Visit any CRDB branch with valid ID and income proof. ");

        prompt.append("13. Jijenge Mortgage — ");
        prompt.append("home loans from TZS 20,000,000 to TZS 1,000,000,000. ");
        prompt.append("Action: Contact CRDB home loans team with property documents. ");

        prompt.append("INSURANCE: ");
        prompt.append("14. Medical Insurance — in-patient out-patient and referral cover. ");
        prompt.append("Action: Contact CRDB insurance team for a quote. ");

        prompt.append("15. Personal Accident Policy — personal accident insurance cover. ");
        prompt.append("Action: Contact CRDB insurance team for a quote. ");

        prompt.append("Now recommend exactly 5 products from the above list that best match this customer's profile. ");
        prompt.append("Consider their age, risk appetite, available capital, investment goals, and existing products carefully.");

        return prompt.toString();
    }

    // Call Bedrock and get product recommendations back
    public List<RecommendedProduct> generateRecommendations(
            CustomerProfile profile) throws Exception {

        String prompt = buildPrompt(profile);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("inputText", prompt);

        Map<String, Object> textConfig = new HashMap<>();
        textConfig.put("maxTokenCount", 2048);
        textConfig.put("temperature", 0.5);
        textConfig.put("topP", 0.9);
        requestMap.put("textGenerationConfig", textConfig);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        InvokeModelRequest request = InvokeModelRequest.builder()
            .modelId(MODEL_ID)
            .contentType("application/json")
            .accept("application/json")
            .body(SdkBytes.fromUtf8String(requestBody))
            .build();

        InvokeModelResponse response = bedrockClient.invokeModel(request);

        String responseBody = response.body().asUtf8String();
        JsonNode root = objectMapper.readTree(responseBody);
        String outputText = root.path("results").get(0)
            .path("outputText").asText();

        JsonNode recommendationsNode = objectMapper.readTree(outputText)
            .path("recommendations");

        List<RecommendedProduct> products = new ArrayList<>();
        for (JsonNode node : recommendationsNode) {
            RecommendedProduct product = new RecommendedProduct();
            product.setProductName(node.path("productName").asText());
            product.setCategory(node.path("category").asText());
            product.setReason(node.path("reason").asText());
            product.setEligibilityNote(node.path("eligibilityNote").asText());
            product.setActionToTake(node.path("actionToTake").asText());
            product.setConfidenceScore(node.path("confidenceScore").asDouble());
            products.add(product);
        }

        return products;
    }
}