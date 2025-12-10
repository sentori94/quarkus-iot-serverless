#!/bin/bash

# Script de déploiement pour Quarkus IoT Lambda
# Migration de Spring Boot ECS vers Quarkus Lambda

set -e

STACK_NAME="quarkus-iot-lambda"
REGION="us-east-1"
S3_BUCKET="quarkus-iot-lambda-deployment-${RANDOM}"

echo "=========================================="
echo "Deploying Quarkus IoT Lambda"
echo "Migration: Spring Boot ECS → Quarkus Lambda"
echo "=========================================="

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if SAM CLI is installed
if ! command -v sam &> /dev/null; then
    echo "❌ AWS SAM CLI is not installed. Please install it first."
    exit 1
fi

# Check if S3 bucket exists, create if not
echo "🪣 Checking S3 bucket..."
if ! aws s3 ls "s3://${S3_BUCKET}" 2>&1 > /dev/null; then
    echo "   Creating S3 bucket: ${S3_BUCKET}"
    aws s3 mb "s3://${S3_BUCKET}" --region ${REGION}
else
    echo "   S3 bucket already exists: ${S3_BUCKET}"
fi

# Build the application
echo ""
echo "🔨 Building application..."
./build.sh

# Validate SAM template
echo ""
echo "✅ Validating SAM template..."
sam validate --template template.yaml --region ${REGION}

# Deploy with SAM
echo ""
echo "🚀 Deploying with AWS SAM..."
sam deploy \
    --template-file template.yaml \
    --stack-name ${STACK_NAME} \
    --s3-bucket ${S3_BUCKET} \
    --region ${REGION} \
    --capabilities CAPABILITY_IAM \
    --no-fail-on-empty-changeset \
    --parameter-overrides \
        GrafanaBaseUrl="http://localhost:3000" \
        GrafanaDashboardPath="/d/iot-dashboard"

# Get stack outputs
echo ""
echo "📊 Retrieving stack outputs..."
API_ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name ${STACK_NAME} \
    --region ${REGION} \
    --query "Stacks[0].Outputs[?OutputKey=='ApiEndpoint'].OutputValue" \
    --output text)

LAMBDA_ARN=$(aws cloudformation describe-stacks \
    --stack-name ${STACK_NAME} \
    --region ${REGION} \
    --query "Stacks[0].Outputs[?OutputKey=='LambdaFunctionArn'].OutputValue" \
    --output text)

echo ""
echo "=========================================="
echo "✅ Deployment completed successfully!"
echo "=========================================="
echo "🌐 API Endpoint: ${API_ENDPOINT}"
echo "⚡ Lambda ARN: ${LAMBDA_ARN}"
echo ""
echo "🧪 Test the API:"
echo "   Health Check:"
echo "   curl ${API_ENDPOINT}api/health"
echo ""
echo "   Swagger UI:"
echo "   ${API_ENDPOINT}swagger-ui"
echo ""
echo "📋 View logs:"
echo "   sam logs --stack-name ${STACK_NAME} --tail"
echo ""
echo "🗑️  Delete stack:"
echo "   sam delete --stack-name ${STACK_NAME}"
echo ""
