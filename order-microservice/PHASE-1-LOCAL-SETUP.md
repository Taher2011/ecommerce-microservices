Phase-1 focuses on clean local development using Spring profiles and IDE run configurations, without introducing Docker or externalized .env files.

Run Order Microservice locally in Eclipse using:

Spring Boot

PostgreSQL (local)

Environment-specific configs (dev / stage)

Credentials via Run Configuration

No Docker, No .env file
================================================================================================================

Step 1: Create AWS credentials files (Windows)

Create folder:

C:\Users\admin\.aws\

Create files without extension:

credentials
[dev]
aws_access_key_id=XXXXXXXX
aws_secret_access_key=YYYYYYYY

config
[profile dev]
region=us-east-1
================================================================================================================
Step 2: Tell AWS SDK which profile to use

In Eclipse → Run Configuration → VM arguments add:
-Daws.profile=dev
================================================================================================================
Step 3: Run Spring Boot normally

Run with Spring profile:

spring.profiles.active=dev


AWS SDK automatically loads credentials from:
~/.aws/credentials (dev profile)
================================================================================================================
Step 4: Done ✅

No access keys in code or YAML
Same code works on EC2 using IAM Role
🧠 One-Line Reminder
Spring profile ≠ AWS profile.
AWS profile is selected using -Daws.profile.
================================================================================================================

