package org.lambadaframework.deployer;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.lambadaframework.deployer.aws.LambdaFunction;
import org.lambadaframework.deployer.aws.ApiGateway;
import org.lambadaframework.deployer.aws.Cloudformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.*;


@Mojo(name = "deploy-lambda")
public class LambadaDeployer extends AbstractMojo {

    @Parameter(required = true)
    public String packageName;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    public MavenProject mavenProject;

    @Parameter(required = true)
    public String stageToDeploy;

    @Parameter
    public String versionToDeploy;

    /**
     * Specifies the region where the application will be deployed.
     * Should be a valid AWS Region with Lambda and API Gateway support
     */
    @Parameter(required = true)
    public String regionToDeploy;

    /**
     * Specifies the maximum execution time allowed for Lambda function (seconds)
     */
    @Parameter(defaultValue = "3")
    public String lambdaMaximumExecutionTime = "3";

    /**
     * Specifies the maximum memory size allowed for Lambda function (MB)
     */
    @Parameter(defaultValue = "128")
    public String lambdaMemorySize = "128";

    /**
     * Lambda execution role policy ARN
     * <p>
     * This policy is attached to the default Lambda execution profile.
     * <p>
     * With this policy, Lambda function can access to the resources allowed in the policy.
     * <p>
     * In case of this property is not set, a default policy with sufficient permissions
     * to execute Lambda function in a VPC is assigned to the Lambda function.
     */
    @Parameter
    public List<CharSequence> lambdaExecutionRolePolicies;


    @Parameter
    public List<String> lambdaSecurityGroups;

    @Parameter
    public List<String> lambdaSubnetIds;

    public static final String LOG_SEPERATOR = new String(new char[72]).replace("\0", "-");


    /**
     * Applies Cloudformation template.
     * <p>
     * Built-in Cloudformation template creates Lambda function and the necessary IAM Roles.
     * <p>
     * If CF template does not exist it creates a new one.
     *
     * @param deployment Deployment
     * @return CloudFormationOutput
     * @throws Exception
     */
    protected Cloudformation.CloudFormationOutput applyCloudFormation(Deployment deployment) throws Exception {
        Cloudformation cloudformation = new Cloudformation(deployment);
        cloudformation.setLog(getLog());
        return cloudformation.createOrUpdateStack();
    }


    /**
     * Checks region for valid values.
     * <p>
     * We do not need the return value, so we just check if an exception is thrown or no.
     *
     * @param region Region to check
     */
    public void checkRegion(String region) {
        try {
            Region.getRegion(Regions.fromName(region));
        } catch (Exception e) {
            throw new RuntimeException(region + " is not a AWS region. Please select a valid one.");
        }
    }


    public Deployment getDeployment() {

        Properties cloudFormationParameters = new Properties();
        cloudFormationParameters.setProperty(Deployment.LAMBDA_MAXIMUM_EXECUTION_TIME_KEY, lambdaMaximumExecutionTime);
        cloudFormationParameters.setProperty(Deployment.LAMBDA_MEMORY_SIZE_KEY, lambdaMemorySize);

        if (lambdaExecutionRolePolicies != null) {
            cloudFormationParameters.setProperty(Deployment.LAMBDA_EXECUTION_ROLE_POLICY_KEY, String.join(",", lambdaExecutionRolePolicies));
        }

        Deployment deployment = new Deployment(
                mavenProject,
                packageName,
                cloudFormationParameters,
                regionToDeploy,
                stageToDeploy);
        deployment.setLog(getLog());
        if (lambdaSecurityGroups != null && lambdaSubnetIds != null) {
            deployment.setLambdaSecurityGroups(lambdaSecurityGroups);
            deployment.setLambdaSubnetIds(lambdaSubnetIds);
        } else if (lambdaSecurityGroups != null || lambdaSubnetIds != null) {
            throw new RuntimeException("lambdaSecurityGroups and lambdaSubnetIds should be set together.");
        }
        return deployment;
    }

    /**
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        try {

            Deployment deployment = getDeployment();
            getLog().info(LOG_SEPERATOR);
            getLog().info("__     ");
            getLog().info("\\ \\    ");
            getLog().info(" \\ \\   ");
            getLog().info("  > \\  ");
            getLog().info(" / ^ \\ ");
            getLog().info("/_/ \\_\\");
            getLog().info("Deployment to AWS Lambda and Gateway is starting.");

            checkRegion(regionToDeploy);
            getLog().info("Group Id: " + mavenProject.getGroupId());
            getLog().info("Artifact Id: " + mavenProject.getArtifactId());
            getLog().info("Version to deploy: " + deployment.getVersion());
            getLog().info("Stage to deploy: " + stageToDeploy);
            getLog().info("Region to deploy: " + regionToDeploy);
            getLog().info(LOG_SEPERATOR);

            getLog().info("CLOUDFORMATION");
            Cloudformation.CloudFormationOutput cloudFormationOutput = applyCloudFormation(deployment);
            getLog().info("Deployed IAM Role: " + cloudFormationOutput.getLambdaExecutionRole());
            getLog().info("Deployed Lambda Function ARN: " + cloudFormationOutput.getLambdaFunctionArn());
            getLog().info(LOG_SEPERATOR);

            /**
             * Set up VPC of Lambda, create new version
             */
            getLog().info("LAMBDA");
            LambdaFunction lambdaFunction = new LambdaFunction(cloudFormationOutput.getLambdaFunctionArn(), deployment);
            lambdaFunction.setLog(getLog());
            String functionArn = lambdaFunction.deployLatestVersion();
            getLog().info(LOG_SEPERATOR);

            getLog().info("API GATEWAY");
            ApiGateway apiGateway = new ApiGateway(deployment, functionArn, cloudFormationOutput.getLambdaExecutionRole());
            apiGateway.setLog(getLog());
            apiGateway.deployEndpoints();
            getLog().info(LOG_SEPERATOR);


        } catch (Exception e) {
            throw new MojoExecutionException("Exception at deployment", e);
        }
    }


}


