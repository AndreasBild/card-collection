# AWS Deployment Guide

This guide provides instructions on how to set up the required AWS infrastructure to run this application in a cloud environment.

## 1. Create an RDS MySQL Instance

First, you need to create a managed MySQL database using Amazon RDS.

1.  **Navigate to the RDS console** in the AWS Management Console.
2.  Click **Create database** and select **MySQL** as the engine type.
3.  Choose a template that meets your needs (e.g., "Dev/Test" for a development environment).
4.  Under **Settings**, provide a unique identifier for your DB instance, and configure the master username and password. **Store these credentials securely**, as you will need them later.
5.  Configure the instance size, storage, and other settings according to your requirements.
6.  In the **Connectivity** section, ensure that the RDS instance is accessible from the environment where your application will be deployed (e.g., an EC2 instance or a container service). You may need to configure VPC security groups to allow inbound traffic on the MySQL port (3306).
7.  Click **Create database** to launch the instance.

## 2. Store Credentials in AWS Secrets Manager

To avoid hardcoding database credentials in the application, we use AWS Secrets Manager to store them securely.

1.  **Navigate to the Secrets Manager console**.
2.  Click **Store a new secret**.
3.  Select **Credentials for RDS database** as the secret type.
4.  Enter the **username** and **password** that you configured when creating the RDS instance.
5.  Select the **RDS DB instance** you created earlier.
6.  Give the secret a descriptive name, such as `card-collection/db-credentials`.
7.  Follow the remaining steps to create the secret.
8.  **Important:** After the secret is created, you must manually add the database name to it.
    *   Navigate to the secret you just created.
    *   Click **Retrieve secret value**, then click **Edit**.
    *   Add a new key-value pair:
        *   **Key:** `dbname`
        *   **Value:** The name of your database (e.g., `card_collection`).
    *   Click **Save**.

## 3. Configure IAM Permissions

Your application needs permission to retrieve the secret from AWS Secrets Manager. This is typically done by creating an IAM role with the necessary permissions and attaching it to the environment where your application is running (e.g., an EC2 instance profile or a task role in ECS).

1.  **Navigate to the IAM console**.
2.  Go to **Roles** and click **Create role**.
3.  Select the AWS service that will host your application (e.g., **EC2**).
4.  Attach a policy that grants permission to read the secret. You can create a new inline policy with the following JSON, replacing `<your-region>`, `<your-account-id>`, and `<secret-name>` with your specific values:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowReadSecretsManager",
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue"
            ],
            "Resource": "arn:aws:secretsmanager:<your-region>:<your-account-id>:secret:<secret-name>-*"
        }
    ]
}
```

5.  Complete the role creation process and attach the role to your application's environment.

## 4. Activate the `aws` Profile

To enable the AWS-specific configuration, you need to activate the `aws` Spring profile when deploying the application. This is typically done by setting an environment variable.

For example, if you are deploying to an EC2 instance or a container, set the following environment variable:

`SPRING_PROFILES_ACTIVE=aws`

This will instruct Spring Boot to load the configuration from `application-aws.properties`, which is configured to fetch the database credentials from AWS Secrets Manager.

Once you have completed these steps, your application will be able to securely fetch the database credentials from AWS Secrets Manager and connect to the RDS instance.
