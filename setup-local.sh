#!/bin/bash

echo "Creating DynamoDB tables locally..."

# Create customer-profiles table
aws dynamodb create-table \
    --table-name customer-profiles \
    --attribute-definitions \
        AttributeName=customerId,AttributeType=S \
    --key-schema \
        AttributeName=customerId,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://localhost:8000

# Create recommendations table
aws dynamodb create-table \
    --table-name recommendations \
    --attribute-definitions \
        AttributeName=customerId,AttributeType=S \
        AttributeName=generatedAt,AttributeType=S \
    --key-schema \
        AttributeName=customerId,KeyType=HASH \
        AttributeName=generatedAt,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://localhost:8000

echo "Tables created successfully"

echo "Creating demo customer profile..."

# Create a demo customer
aws dynamodb put-item \
    --table-name customer-profiles \
    --item '{
        "customerId": {"S": "CRDB-DEMO-001"},
        "fullName": {"S": "William Makori"},
        "email": {"S": "william@example.com"},
        "age": {"N": "25"},
        "employmentStatus": {"S": "Salaried"},
        "monthlyIncomeRange": {"S": "1,000,000 - 3,000,000"},
        "riskAppetite": {"S": "Medium"},
        "investmentHorizon": {"S": "Medium-term (1 to 5 years)"},
        "availableCapital": {"N": "5000000"},
        "investmentGoals": {"SS": ["Wealth Growth", "Retirement Planning"]},
        "createdAt": {"S": "2026-06-01T00:00:00Z"},
        "updatedAt": {"S": "2026-06-01T00:00:00Z"}
    }' \
    --endpoint-url http://localhost:8000

echo "Demo customer created successfully"
echo "Customer ID: CRDB-DEMO-001"