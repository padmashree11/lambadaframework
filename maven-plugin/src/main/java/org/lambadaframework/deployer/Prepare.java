package org.lambadaframework.deployer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.lambadaframework.AbstractMojoPlugin;
import org.lambadaframework.aws.S3;

/**
 * Prepare goal creates S3 deployment bucket if it does not exist
 * It should run before DEPLOY phase, and INSTALL is a good candidate to run this
 */
@Mojo(name = "prepare",
        defaultPhase = LifecyclePhase.INSTALL,
        requiresOnline = true
)
public class Prepare extends AbstractMojoPlugin {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Deployment deployment = getDeployment();
            getLog().info("Creating deployment bucket if not exists.");
            if (S3.createBucketIfNotExists(deployment.getBucketName(),
                    deployment.getRegion())) {
                getLog().info("Created the deployment bucket.");
            } else {
                getLog().info("Deployment bucket already exist.");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Exception at deployment", e);
        }
    }
}
