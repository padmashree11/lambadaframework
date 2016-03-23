package org.lambadaframework.aws;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.model.Resource;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import org.lambadaframework.deployer.Deployment;

public class LambdaFunction extends AWSTools {

    protected String functionArn;

    protected Deployment deployment;

    protected AWSLambda lambdaClient;

    protected static final String API_GATEWAY_PRINCIPAL = "apigateway.amazonaws.com";
    protected static final String POLICY_ACTION = "lambda:InvokeFunction";


    public LambdaFunction(String functionArn, Deployment deployment) {
        this.functionArn = functionArn;
        this.deployment = deployment;
    }


    protected AWSLambda getLambdaClient() {

        if (lambdaClient != null) {
            return lambdaClient;
        }

        return lambdaClient = new AWSLambdaClient(getAWSCredentialsProvideChain()).withRegion(Region.getRegion(Regions.fromName(deployment.getRegion())));
    }

    /**
     * Deploys the latest version to Lambda.
     */
    public String deployLatestVersion() {
        if (log != null) {
            log.info("Deploying version " + deployment.getVersion() + " to " + deployment.getStage() + " to Lambda function as $LATEST. (" + functionArn + ")");
        }

        updateCode();
        setupVPC();
        return publishVersion();
    }


    /**
     * Updates code
     */
    protected String updateCode() {
        if (log != null) {
            log.info("JAR FILE Location: s3://" + deployment.getBucketName() + "/" + deployment.getJarFileLocationOnS3());
        }
        UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest();
        updateFunctionCodeRequest.setFunctionName(functionArn);
        updateFunctionCodeRequest.setS3Bucket(deployment.getBucketName());
        updateFunctionCodeRequest.setS3Key(deployment.getJarFileLocationOnS3());
        updateFunctionCodeRequest.setPublish(true);
        UpdateFunctionCodeResult updateFunctionCodeResult = getLambdaClient().updateFunctionCode(updateFunctionCodeRequest);
        if (log != null) {
            log.info("Lambda function is at version: " + updateFunctionCodeResult.getVersion());
        }

        return updateFunctionCodeResult.getVersion();

    }

    /**
     * Sets up VPC Configuration
     */
    protected void setupVPC() {

        if (deployment.getLambdaSecurityGroups() == null || deployment.getLambdaSubnetIds() == null) {
            if (log != null) {
                log.info("VPC is not set, skipping.");
            }
            return;
        }

        if (log != null) {
            log.info("VPC is being configured for Lambda function.");
        }

        VpcConfig vpcConfig = new VpcConfig();
        vpcConfig.setSecurityGroupIds(deployment.getLambdaSecurityGroups());
        vpcConfig.setSubnetIds(deployment.getLambdaSubnetIds());


        UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest();
        updateFunctionConfigurationRequest.setVpcConfig(vpcConfig);
        updateFunctionConfigurationRequest.setFunctionName(functionArn);
        UpdateFunctionConfigurationResult updateFunctionConfigurationResult = getLambdaClient().updateFunctionConfiguration(updateFunctionConfigurationRequest);

        if (log != null && updateFunctionConfigurationResult.getVpcConfig() != null) {
            log.info("VPC has been configured for Lambda function as follows:");
            log.info("VPC ID: " + updateFunctionConfigurationResult.getVpcConfig().getVpcId());
            log.info("Subnets: " + updateFunctionConfigurationResult.getVpcConfig().getSubnetIds().toString());
            log.info("Security groups: " + updateFunctionConfigurationResult.getVpcConfig().getSecurityGroupIds().toString());
        }
    }


    /**
     * @return Alias ARN
     */
    protected String publishVersion() {
        if (log != null) {
            log.info("Publishing a new version of Lambda function and marking as version " + deployment.getVersion());
        }


        PublishVersionRequest publishVersionRequest = new PublishVersionRequest();
        publishVersionRequest.setFunctionName(functionArn);
        publishVersionRequest.setDescription(deployment.getLambdaDescription());
        PublishVersionResult publishVersionResult = getLambdaClient().publishVersion(publishVersionRequest);

        if (log != null) {
            log.info("New Lambda function is published as version " + publishVersionResult.getVersion());
        }


        /**
         * Create alias if not exist
         */
        createAlias(functionArn, deployment.getVersion(), publishVersionResult.getVersion());
        String aliasArn = setAliasVersion(functionArn, deployment.getVersion(), publishVersionResult.getVersion());

        if (log != null) {
            log.info(publishVersionResult.getVersion() + " has been marked as " + deployment.getVersion());
            log.info("Alias ARN to be used in API Gateway is: " + aliasArn);
        }

        return aliasArn;
    }


    /**
     * AWS does not support . in Lambda aliases. So we replace them with "-"
     *
     * @param version Version
     * @return AWS Friendly version number
     */
    private String createLambdaFriendlyVersionName(String version) {
        return version.replace(".", "-");
    }

    protected boolean createAlias(String functionArn, String version, String functionVersion) {
        try {
            CreateAliasRequest createAliasRequest = new CreateAliasRequest();
            createAliasRequest.setFunctionName(functionArn);
            createAliasRequest.setFunctionVersion(functionVersion);
            createAliasRequest.setName(createLambdaFriendlyVersionName(version));
            getLambdaClient().createAlias(createAliasRequest);
            return true;
        } catch (ResourceConflictException e) {
            return false;
        }
    }

    private String setAliasVersion(String functionArn, String version, String functionVersion) {
        UpdateAliasRequest updateAliasRequest = new UpdateAliasRequest();
        updateAliasRequest.setFunctionVersion(functionVersion);
        updateAliasRequest.setFunctionName(functionArn);
        updateAliasRequest.setName(createLambdaFriendlyVersionName(version));
        UpdateAliasResult updateAliasResult = getLambdaClient().updateAlias(updateAliasRequest);
        return updateAliasResult.getAliasArn();
    }

    public void givePermissionForApiGatewayEndpoint(Resource apiGatewayResource) {

        String policyId = "api_gateway_policy";

        if (log != null) {
            log.info("Lambda permission is being granted for endpoint " + apiGatewayResource.getId());
        }

        try {
            getLambdaClient().removePermission(new RemovePermissionRequest()
                    .withFunctionName(functionArn)
                    .withStatementId(policyId)
            );
        } catch (ResourceNotFoundException e) {
            /**
             * Permission does not exist, do nothing
             */
        }

        getLambdaClient().addPermission(new AddPermissionRequest()
                .withAction(POLICY_ACTION)
                .withFunctionName(functionArn)
                .withPrincipal(API_GATEWAY_PRINCIPAL)
                .withStatementId(policyId)
        );

        if (log != null) {
            log.info("Lambda permission has been granted for endpoint " + apiGatewayResource.getId());
        }
    }
}
