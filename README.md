# CRDB Customer Advisor

An AI-powered financial product recommendation engine for CRDB Bank customers. The system analyzes a customer's financial profile and recommends the most suitable CRDB banking and capital markets products — personalized, intelligent, and built on a serverless AWS architecture.

---

## What This Project Does

A CRDB Bank customer logs into their personal dashboard, fills in their financial profile, and the system uses Amazon Bedrock's generative AI to recommend the most relevant CRDB products from across the bank's full catalogue — from everyday banking accounts to capital markets investment products. Recommendations are personalized, explained in plain language, and updated automatically as the customer's profile changes.

Think of it as a 24/7 personal financial advisor that knows every CRDB product and matches them to the right customer.

---

## Products the AI Recommends From

### Capital Markets Products
| Product | Description |
|---|---|
| **Government Securities** | Treasury Bills (short-term) and Treasury Bonds (long-term) issued by the Government of Tanzania |
| **Collective Investment Schemes** | Pooled investment funds across shares, bonds and fixed deposits |
| **Securities Trading** | Buy and sell equities and bonds listed on the Dar es Salaam Stock Exchange (DSE) |
| **IPO Management** | Participate in new public offerings of shares and bonds |
| **Corporate Finance & Advisory** | Capital raising, IPO structuring, debt issuance, private placements |
| **Custodial Services** | Safekeeping and administration of financial assets |
| **Registrar Services** | Investor registry management for equity and debt securities |
| **Retirement Planning (Staafu Kibabe)** | Long-term retirement cash flow and financial stability planning |
| **Financial Doctor** | One-on-one financial advisory and portfolio assessment for new investors |
| **Investment Management** | Discretionary portfolio management for HNWIs (min. TZS 250M) |
| **Research & Analysis** | Market intelligence, economic outlook reports, and investment insights |

### Personal Banking Products
| Product | Description |
|---|---|
| **Current Account** | Everyday operating account |
| **Savings Account** | Save for future needs |
| **Salary Account** | Facilitates salary payments |
| **Fixed Deposit (Thamani)** | Deposit funds for a fixed period at guaranteed interest |
| **Dhahabu Account** | Regular monthly savings with fixed amounts |
| **Busara Account** | Savings account for shareholders |
| **Personal Loan** | Loan facility for personal growth |
| **Jijenge Mortgage** | Home loans from TZS 20M to 1 Billion |
| **Salary Advance** | Instant short-term loan against salary |
| **Jinasue** | Flexible overdraft facility |

### Insurance Products
| Product | Description |
|---|---|
| **Medical Insurance** | In-patient, out-patient and referral cover |
| **Credit Life Assurance** | Cover against financial loss for creditors |
| **Personal Accident Policy** | Personal accident insurance cover |
| **Group Life Insurance** | Family protection from unforeseen risks |
| **Motor Insurance** | Vehicle loss and damage protection |

---

## Architecture

```
Customer Dashboard (TypeScript/React)
          ↓
API Gateway (HTTP Routes)
          ↓
AWS Lambda (Java Handlers)
          ↓
    ┌─────────────────────────┐
    │  RecommendationService  │
    │     (Orchestrator)      │
    └─────────────────────────┘
          ↓               ↓
Amazon Bedrock        DynamoDB
(AI Generation)    (Data Persistence)
          ↓               ↓
    └─────────────────────────┘
                ↓
          CloudWatch
         (Monitoring)
```

---

## Tech Stack

| Technology | Role |
|---|---|
| **Java 17** | Backend Lambda function logic |
| **TypeScript** | Frontend dashboard |
| **React** | Frontend UI framework |
| **AWS Lambda** | Serverless compute — runs backend on demand |
| **Amazon Bedrock** | Generative AI — produces personalized product recommendations |
| **DynamoDB** | NoSQL database — stores customer profiles and recommendations |
| **API Gateway** | HTTP routing — connects frontend to Lambda functions |
| **AWS CodePipeline** | CI/CD — automates testing and deployment |
| **AWS CodeBuild** | Builds and tests Java code on every push |
| **CloudWatch** | Logging, metrics, and monitoring |
| **Maven** | Java build tool and dependency management |
| **Vite** | Frontend build tool |

---

## Project Structure

```
CRDBCustomerAdvisor/
│
├── backend/
│   ├── src/
│   │   └── main/java/com/crdb/advisor/
│   │       ├── handler/
│   │       │   ├── GetRecommendationsHandler.java
│   │       │   ├── UpdateProfileHandler.java
│   │       │   └── GetProductsHandler.java
│   │       ├── model/
│   │       │   ├── CustomerProfile.java
│   │       │   └── ProductRecommendation.java
│   │       ├── repository/
│   │       │   ├── CustomerProfileRepository.java
│   │       │   └── RecommendationRepository.java
│   │       └── service/
│   │           ├── BedrockService.java
│   │           └── RecommendationService.java
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Dashboard.tsx
│   │   │   ├── RecommendationCard.tsx
│   │   │   ├── ProfileForm.tsx
│   │   │   └── ProductCatalogue.tsx
│   │   ├── hooks/
│   │   │   └── useRecommendations.ts
│   │   ├── services/
│   │   │   └── api.ts
│   │   ├── types/
│   │   │   └── index.ts
│   │   ├── App.tsx
│   │   └── main.tsx
│   └── package.json
│
├── infrastructure/
│   ├── dynamodb/
│   │   └── tables.json
│   ├── lambda/
│   │   └── functions.json
│   └── apigateway/
│       └── api.json
│
├── buildspec.yml
└── README.md
```

---

## Customer Profile

The AI uses the following customer data to generate recommendations:

| Field | Description | Example |
|---|---|---|
| `customerId` | Unique customer identifier | `CRDB-001` |
| `age` | Customer age | `34` |
| `employmentStatus` | Employment type | `Salaried`, `Self-employed`, `Retired` |
| `monthlyIncome` | Monthly income range in TZS | `500,000 - 1,000,000` |
| `riskAppetite` | Investment risk tolerance | `Low`, `Medium`, `High` |
| `investmentGoals` | What the customer wants to achieve | `Retirement`, `Wealth Growth`, `Capital Preservation` |
| `existingProducts` | Products the customer already holds | `Savings Account`, `Personal Loan` |
| `investmentHorizon` | How long they plan to invest | `Short-term`, `Medium-term`, `Long-term` |
| `availableCapital` | Amount available to invest in TZS | `5,000,000` |

---

## How a Recommendation is Generated

```
Customer opens dashboard
        ↓
Fills in or updates their financial profile
        ↓
Lambda checks DynamoDB for fresh recommendations (< 24hrs old)
    → Cache HIT? Return immediately
    → Cache MISS? Continue ↓
        ↓
Lambda reads CustomerProfile from DynamoDB
        ↓
Lambda builds a detailed prompt for Bedrock:
"This customer is 34, salaried, earns TZS 800,000/month,
has medium risk appetite, wants long-term wealth growth,
already holds a Savings Account. Recommend 5 CRDB products
from the following catalogue with reasons and confidence scores."
        ↓
Amazon Bedrock generates personalized recommendations
        ↓
Lambda saves recommendations to DynamoDB (TTL: 24hrs)
        ↓
Recommendations returned to dashboard
        ↓
CloudWatch logs every step
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/recommendations/{customerId}` | Get AI recommendations for a customer |
| `POST` | `/customers/{customerId}/profile` | Update a customer's financial profile |
| `GET` | `/customers/{customerId}/profile` | Get a customer's current profile |
| `GET` | `/products` | Get the full CRDB product catalogue |

---

## DynamoDB Tables

### customer-profiles
| Attribute | Type | Role |
|---|---|---|
| `customerId` | String | Partition Key |
| `age` | Number | Customer age |
| `employmentStatus` | String | Employment type |
| `monthlyIncome` | String | Income range |
| `riskAppetite` | String | Low / Medium / High |
| `investmentGoals` | String Set | Selected investment goals |
| `existingProducts` | String Set | Products already held |
| `investmentHorizon` | String | Short / Medium / Long term |
| `availableCapital` | Number | Capital available to invest |
| `expiresAt` | Number | TTL — auto deletes after 90 days inactive |

### recommendations
| Attribute | Type | Role |
|---|---|---|
| `customerId` | String | Partition Key |
| `generatedAt` | String | Sort Key |
| `items` | List | AI generated product recommendations |
| `modelUsed` | String | Bedrock model that generated recommendations |
| `expiresAt` | Number | TTL — auto deletes after 24 hours |

---

## Key Design Decisions

**CRDB product knowledge baked into the prompt** — All CRDB products from both the Capital Markets booklet and the broader product catalogue are embedded directly into the Bedrock prompt as structured data. This means the AI always recommends real, accurate CRDB products by name without needing to call any external data source.

**Recommendation caching** — Before calling Bedrock, the system checks DynamoDB for fresh recommendations less than 24 hours old. This avoids unnecessary AI calls, reducing latency and cost.

**TTL-based cost optimization** — DynamoDB TTL automatically deletes stale recommendations after 24 hours and inactive profiles after 90 days, reducing storage costs significantly.

**Risk-matched recommendations** — The Bedrock prompt explicitly instructs the AI to match products to the customer's declared risk appetite. A low-risk customer will never be recommended Securities Trading or IPO participation — they'll get Government Securities and Fixed Deposits instead.

**Eligibility awareness** — The prompt includes product eligibility requirements from the CRDB booklet. For example, Investment Management requires a minimum of TZS 250M — the AI will not recommend this to a customer with TZS 5M available capital.

---

## Local Development Setup

### Prerequisites

| Tool | Version | Download |
|---|---|---|
| Java | 17 | [corretto.aws](https://corretto.aws) or `brew install --cask temurin@17` |
| Maven | 3.9+ | [maven.apache.org](https://maven.apache.org) or `brew install maven` |
| Node.js | 18+ LTS | [nodejs.org](https://nodejs.org) |
| Git | Latest | [git-scm.com](https://git-scm.com) |
| AWS CLI | v2 | [aws.amazon.com/cli](https://aws.amazon.com/cli) |

### Backend Setup

```bash
cd backend
mvn install
mvn test
mvn package
```

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Open your browser at `http://localhost:5173`

---

## Deployment

### Environment Variables

Create a `.env` file in the `frontend` folder:
```
VITE_API_URL=https://your-api-gateway-url.amazonaws.com/prod
```

### Deploy Backend to AWS Lambda

```bash
cd backend
mvn package
aws lambda update-function-code \
  --function-name GetRecommendationsHandler \
  --zip-file fileb://target/crdb-customer-advisor-1.0.0.jar
```

### CI/CD Pipeline

Every push to `main` automatically triggers CodePipeline which compiles, tests, and deploys the updated code to Lambda via CodeBuild.

---

## Monitoring

All Lambda functions log to CloudWatch automatically. Key metrics tracked:

- Lambda invocation count and error rate
- Bedrock API response latency
- DynamoDB read/write capacity consumption
- Recommendation cache hit rate
- Customer profile update frequency

---

## Author

William Makori
Computing Science — Simon Fraser University
GitHub: [github.com/WilliamMakori](https://github.com/WilliamMakori)
Capital Markets Data Source: CRDB Bank Plc — Capital Markets Products and Services Offering
