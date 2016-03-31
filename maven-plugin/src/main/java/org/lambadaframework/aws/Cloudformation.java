package org.lambadaframework.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.*;
import org.lambadaframework.deployer.Deployment;

import java.util.List;

public class Cloudformation extends AWSTools {

    private final static String CLOUDFORMATION_TEMPLATE = "{\n" +
            "  \"AWSTemplateFormatVersion\": \"2010-09-09\",\n" +
            "  \"Description\": \"Instela\",\n" +
            "  \"Parameters\": {\n" +
            "    \"LambdaMemorySize\": {\n" +
            "      \"Type\": \"Number\",\n" +
            "      \"Default\": \"128\",\n" +
            "      \"Description\": \"AWS Lambda Function Maximum Allowed Memory.\"\n" +
            "    },\n" +
            "    \"LambdaMaximumExecutionTime\": {\n" +
            "      \"Type\": \"Number\",\n" +
            "      \"Default\": \"3\",\n" +
            "      \"Description\": \"AWS Lambda Function Maximum Execution Time (seconds).\"\n" +
            "    },\n" +
            "    \"DeploymentS3Bucket\": {\n" +
            "      \"Description\": \"Deployment S3 Bucket is where project is deployed after mvn deploy command.\",\n" +
            "      \"Type\": \"String\",\n" +
            "      \"MinLength\": \"1\",\n" +
            "      \"MaxLength\": \"25\"\n" +
            "    },\n" +
            "    \"DeploymentS3Key\": {\n" +
            "      \"Description\": \"Deployment S3 Key is the S3 Path where project is deployed after mvn deploy command.\",\n" +
            "      \"Type\": \"String\",\n" +
            "      \"MinLength\": \"1\"\n" +
            "    },\n" +
            "    \"LambdaDescription\": {\n" +
            "      \"Description\": \"Lambda Description\",\n" +
            "      \"Type\": \"String\",\n" +
            "      \"MinLength\": \"1\"\n" +
            "    },\n" +
            "    \"LambdaExecutionRoleManagedPolicyARNs\": {\n" +
            "      \"Description\": \"Managed Policy ARNs for Lambda Execution IAM Role\",\n" +
            "      \"Type\": \"CommaDelimitedList\",\n" +
            "      \"Default\": \"arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"Mappings\": {\n" +
            "  },\n" +
            "  \"Resources\": {\n" +
            "    \"LambadaExecutionRole\": {\n" +
            "      \"Type\": \"AWS::IAM::Role\",\n" +
            "      \"Properties\": {\n" +
            "        \"AssumeRolePolicyDocument\": {\n" +
            "          \"Version\": \"2012-10-17\",\n" +
            "          \"Statement\": [\n" +
            "            {\n" +
            "              \"Effect\": \"Allow\",\n" +
            "              \"Principal\": {\n" +
            "                \"Service\": [\n" +
            "                  \"lambda.amazonaws.com\",\n" +
            "                  \"apigateway.amazonaws.com\"\n" +
            "                ]\n" +
            "              },\n" +
            "              \"Action\": [\n" +
            "                \"sts:AssumeRole\"\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"ManagedPolicyArns\": {\n" +
            "          \"Ref\": \"LambdaExecutionRoleManagedPolicyARNs\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"LambadaExecutionPolicy\": {\n" +
            "      \"Type\": \"AWS::IAM::Policy\",\n" +
            "      \"Properties\": {\n" +
            "        \"PolicyName\": \"${stage}-${project}-lambda\",\n" +
            "        \"PolicyDocument\": {\n" +
            "          \"Version\": \"2012-10-17\",\n" +
            "          \"Statement\": [\n" +
            "            {\n" +
            "              \"Effect\": \"Allow\",\n" +
            "              \"Action\": [\n" +
            "                \"ec2:CreateNetworkInterface\",\n" +
            "                \"ec2:DescribeNetworkInterfaces\",\n" +
            "                \"ec2:DeleteNetworkInterface\"\n" +
            "              ],\n" +
            "              \"Resource\": \"*\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"Action\": [\n" +
            "                \"logs:CreateLogGroup\",\n" +
            "                \"logs:CreateLogStream\",\n" +
            "                \"logs:PutLogEvents\"\n" +
            "              ],\n" +
            "              \"Effect\": \"Allow\",\n" +
            "              \"Resource\": \"arn:aws:logs:*\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"Effect\": \"Allow\",\n" +
            "              \"Action\": [\n" +
            "                \"lambda:InvokeFunction\"\n" +
            "              ],\n" +
            "              \"Resource\": [\n" +
            "                \"*\"\n" +
            "              ]\n" +
            "            },\n" +
            "            {\n" +
            "              \"Effect\": \"Allow\",\n" +
            "              \"Action\": [\n" +
            "                \"apigateway:*\",\n" +
            "                \"iam:PassRole\"\n" +
            "              ],\n" +
            "              \"Resource\": [\n" +
            "                \"*\"\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"Roles\": [\n" +
            "          {\n" +
            "            \"Ref\": \"LambadaExecutionRole\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    \"LambdaFunction\": {\n" +
            "      \"Type\": \"AWS::Lambda::Function\",\n" +
            "      \"Properties\": {\n" +
            "        \"Handler\": \"org.lambadaframework.runtime.Handler\",\n" +
            "        \"Role\": {\n" +
            "          \"Fn::GetAtt\": [\n" +
            "            \"LambadaExecutionRole\",\n" +
            "            \"Arn\"\n" +
            "          ]\n" +
            "        },\n" +
            "        \"Code\": {\n" +
            "          \"S3Bucket\": {\n" +
            "            \"Ref\": \"DeploymentS3Bucket\"\n" +
            "          },\n" +
            "          \"S3Key\": {\n" +
            "            \"Ref\": \"DeploymentS3Key\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"Runtime\": \"java8\",\n" +
            "        \"Timeout\": {\n" +
            "          \"Ref\": \"LambdaMaximumExecutionTime\"\n" +
            "        },\n" +
            "        \"MemorySize\": {\n" +
            "          \"Ref\": \"LambdaMemorySize\"\n" +
            "        },\n" +
            "        \"Description\": {\n" +
            "          \"Ref\": \"LambdaDescription\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"Outputs\": {\n" +
            "    \"LambdaExecutionRoleArn\": {\n" +
            "      \"Description\": \"Lambada Execution Role ARN\",\n" +
            "      \"Value\": {\n" +
            "        \"Fn::GetAtt\": [\n" +
            "          \"LambadaExecutionRole\",\n" +
            "          \"Arn\"\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    \"LambdaFunctionArn\": {\n" +
            "      \"Description\": \"Lambada Function ARN\",\n" +
            "      \"Value\": {\n" +
            "        \"Fn::GetAtt\": [\n" +
            "          \"LambdaFunction\",\n" +
            "          \"Arn\"\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";


    private final static String LAMBDA_EXECUTION_IAM_RESOURCE_NAME = "LambdaExecutionRoleArn";
    private final static String LAMBDA_EXECUTION_NAME = "LambdaFunctionArn";

    private AmazonCloudFormationClient cloudformationClient;

    protected Deployment deployment;

    public Cloudformation(Deployment deployment) {
        this.deployment = deployment;
    }

    protected AmazonCloudFormationClient getCloudFormationClient() {
        if (null != cloudformationClient) {
            return cloudformationClient;
        }

        return cloudformationClient = new AmazonCloudFormationClient(getAWSCredentialsProvideChain()).withRegion(Region.getRegion(Regions.fromName(deployment.getRegion())));
    }

    public String getCloudformationTemplate() {
        return CLOUDFORMATION_TEMPLATE
                .replace("${project}", deployment.getProjectName())
                .replace("${stage}", deployment.getStage());

    }


    public static class CloudFormationOutput {

        protected String lambdaExecutionRole;

        protected String lambdaFunctionArn;

        public String getLambdaExecutionRole() {
            return lambdaExecutionRole;
        }

        public CloudFormationOutput setLambdaExecutionRole(String lambdaExecutionRole) {
            this.lambdaExecutionRole = lambdaExecutionRole;
            return this;
        }

        public String getLambdaFunctionArn() {
            return lambdaFunctionArn;
        }

        public CloudFormationOutput setLambdaFunctionArn(String lambdaFunctionArn) {
            this.lambdaFunctionArn = lambdaFunctionArn;
            return this;
        }
    }

    public String waitForCompletion() throws Exception {

        DescribeStacksRequest wait = new DescribeStacksRequest();
        wait.setStackName(deployment.getCloudFormationStackName());
        Boolean completed = false;
        String stackStatus = "Unknown";
        String stackReason = "";

        log.info("Waiting");

        int iteration = 0;
        while (!completed) {
            List<Stack> stacks = getCloudFormationClient().describeStacks(wait).getStacks();
            if (stacks.isEmpty()) {
                completed = true;
                stackStatus = "NO_SUCH_STACK";
                stackReason = "Stack has been deleted";
            } else {
                for (Stack stack : stacks) {
                    if (stack.getStackStatus().equals(StackStatus.CREATE_COMPLETE.toString()) ||
                            stack.getStackStatus().equals(StackStatus.UPDATE_COMPLETE.toString()) ||
                            stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.ROLLBACK_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.DELETE_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.UPDATE_ROLLBACK_COMPLETE.toString())
                            ) {
                        completed = true;
                        stackStatus = stack.getStackStatus();
                        stackReason = stack.getStackStatusReason();
                    }
                }
            }

            // Show we are waiting
            log.info("Please wait (" + ++iteration + ")...");

            // Not done yet so sleep for 2 seconds.
            if (!completed) Thread.sleep(1000);
        }

        // Show we are done
        log.info("Cloudformation update completed.");

        return stackStatus + (stackReason != null ? " (" + stackReason + ")" : "");
    }

    public CloudFormationOutput getStackOutputs(AmazonCloudFormation stackbuilder,
                                                String stackName) {
        DescribeStacksRequest wait = new DescribeStacksRequest();
        wait.setStackName(stackName);
        List<Stack> stacks = getCloudFormationClient().describeStacks(wait).getStacks();

        CloudFormationOutput cloudFormationOutput = new CloudFormationOutput();

        for (Stack stack : stacks) {
            if (stack.getStackName().equals(stackName)) {
                stack.getOutputs().forEach(output -> {
                    if (output.getOutputKey().equals(LAMBDA_EXECUTION_IAM_RESOURCE_NAME)) {
                        cloudFormationOutput.setLambdaExecutionRole(output.getOutputValue());
                    }

                    if (output.getOutputKey().equals(LAMBDA_EXECUTION_NAME)) {
                        cloudFormationOutput.setLambdaFunctionArn(output.getOutputValue());
                    }
                });
                return cloudFormationOutput;
            }
        }
        throw new RuntimeException("Unknown Cloudformation error. Try deploying.");

    }


    public CloudFormationOutput createOrUpdateStack() throws Exception {
        log.info("Creating or updating Cloudformation stack");
        try {
            createStack(deployment, getCloudformationTemplate());
        } catch (AlreadyExistsException alreadyExistsException) {
            log.info("Stack already exists. Trying to update.");
            try {
                updateStack(deployment, getCloudformationTemplate());
            } catch (AmazonServiceException noUpdateNeededException) {
                log.info("No updates needed for Cloudformation. Resuming deployment.");
            }
        }

        return getStackOutputs(getCloudFormationClient(), deployment.getCloudFormationStackName());
    }

    protected void createStack(Deployment deployment,
                               String templateBody) throws Exception {

        String templateName = deployment.getCloudFormationStackName();
        CreateStackRequest createRequest = new CreateStackRequest();
        createRequest.setStackName(templateName);
        createRequest.setTemplateBody(templateBody);
        createRequest.setParameters(deployment.getCloudFormationParameters());
        createRequest.withCapabilities(Capability.CAPABILITY_IAM);
        getCloudFormationClient().createStack(createRequest);
        log.info("Stack creation completed, the stack " + templateName + " completed with " + waitForCompletion());
    }

    protected void updateStack(Deployment deployment,
                               String templateBody) throws Exception {
        String templateName = deployment.getCloudFormationStackName();
        UpdateStackRequest updateStackRequest = new UpdateStackRequest();
        updateStackRequest.setStackName(templateName);
        updateStackRequest.setTemplateBody(templateBody);
        updateStackRequest.setParameters(deployment.getCloudFormationParameters());
        updateStackRequest.withCapabilities(Capability.CAPABILITY_IAM);
        getCloudFormationClient().updateStack(updateStackRequest);
        log.info("Stack update completed, the stack " + templateName + " completed with " + waitForCompletion());
    }

}

