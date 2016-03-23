package org.lambadaframework.aws;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.maven.plugin.logging.Log;

public abstract class AWSTools {

    protected static AWSCredentialsProviderChain getAWSCredentialsProvideChain() {
        return new DefaultAWSCredentialsProviderChain();
    }

    protected Log log;

    private static AmazonS3 s3client;

    protected static AmazonS3 getS3Client() {
        if (null != s3client) {
            return s3client;
        }
        return s3client = new AmazonS3Client(getAWSCredentialsProvideChain());
    }


    public void setLog(Log log) {
        this.log = log;
    }

    protected String getAccountNumberFromArn(String arn) {
        return arn.split(":")[5];
    }

}
