package org.lambadaframework.deployer.aws;

import org.glassfish.jersey.server.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lambadaframework.deployer.Deployment;
import org.lambadaframework.deployer.LambadaDeployer;
import org.apache.maven.project.MavenProject;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Resource.class)
public class ApiGatewayTest {

    protected LambadaDeployer getExampleProject() {
        MavenProject project = new MavenProject();
        project.getProperties().setProperty("deployment.bucket", "maven.cagataygurturk.com");
        project.setGroupId("org.lambadaframework");
        project.setArtifactId("runtime-deploy-maven-plugin");
        project.setVersion("0.0.1");

        Properties props = new Properties();
        props.put("ProjectName", "LambadaTestProject");

        LambadaDeployer deployer = new LambadaDeployer();
        deployer.mavenProject = project;
        deployer.stageToDeploy = "dev";
        deployer.regionToDeploy = "eu-west-1";
        deployer.versionToDeploy = "0.0.1";
        //deployer.cloudFormationParameters = props;
        return deployer;
    }


    protected Deployment getMockDeployment() {

        return mock(Deployment.class);
    }


    @Test
    public void testCleanUpTrailingEndLeadingSlashes() throws Exception {
        String functionArn = "testArn";
        String roleArn = "testArn";
        ApiGateway apiGateway = new ApiGateway(getMockDeployment(), functionArn, roleArn);

        assertEquals("",
                apiGateway.cleanUpTrailingEndLeadingSlashes("/"));

        assertEquals("{id}",
                apiGateway.cleanUpTrailingEndLeadingSlashes("{id}"));


        assertEquals("{id}",
                apiGateway.cleanUpTrailingEndLeadingSlashes("{id}/"));

        assertEquals("{id}",
                apiGateway.cleanUpTrailingEndLeadingSlashes("/{id}"));
    }

    @Test
    public void testFullPartOfResource() throws Exception {
        String functionArn = "testArn";
        String roleArn = "testArn";
        ApiGateway apiGateway = new ApiGateway(getMockDeployment(), functionArn, roleArn);


        final Resource parentMockResource = PowerMock.createMock(Resource.class);
        expect(parentMockResource.getPath())
                .andReturn("/")
                .anyTimes();

        expect(parentMockResource.getParent())
                .andReturn(null)
                .anyTimes();

        final Resource mockResource = PowerMock.createMock(Resource.class);
        expect(mockResource.getPath())
                .andReturn("{id}")
                .anyTimes();

        expect(mockResource.getParent())
                .andReturn(parentMockResource)
                .anyTimes();


        final Resource mockSubResource = PowerMock.createMock(Resource.class);

        expect(mockSubResource.getPath())
                .andReturn("{id}/info")
                .anyTimes();

        expect(mockSubResource.getParent())
                .andReturn(parentMockResource)
                .anyTimes();

        PowerMock.replayAll();

        assertEquals(apiGateway.getFullPartOfResource(parentMockResource), "/");

        assertEquals("/{id}", apiGateway.getFullPartOfResource(mockResource));

        assertEquals("/{id}/info", apiGateway.getFullPartOfResource(mockSubResource));


        final Resource mockParentResource2 = PowerMock.createMock(Resource.class);
        expect(mockParentResource2.getPath())
                .andReturn("/resource")
                .anyTimes();

        expect(mockParentResource2.getParent())
                .andReturn(null)
                .anyTimes();


        final Resource mockSubResource2 = PowerMock.createMock(Resource.class);

        expect(mockSubResource2.getPath())
                .andReturn("{id}/info")
                .anyTimes();

        expect(mockSubResource2.getParent())
                .andReturn(mockParentResource2)
                .anyTimes();

        PowerMock.replayAll();

        assertEquals("/resource/{id}/info", apiGateway.getFullPartOfResource(mockSubResource2));

    }


    @Test
    public void testGetPathPartOfResource() throws Exception {
        String functionArn = "testArn";
        String roleArn = "testArn";

        ApiGateway apiGateway = new ApiGateway(getMockDeployment(), functionArn, roleArn);

        final Resource mockResource = PowerMock.createMock(Resource.class);
        expect(mockResource.getPath())
                .andReturn("/resource1")
                .times(1);

        expect(mockResource.getPath())
                .andReturn("/")
                .times(2);

        PowerMock.replay(mockResource);

        assertEquals(apiGateway.getPathPartOfResource(mockResource),
                "resource1");

        assertEquals(apiGateway.getPathPartOfResource(mockResource),
                "");
    }

    @Test
    public void testGetParentPartOfResource() throws Exception {
        String functionArn = "testArn";
        String roleArn = "testArn";

        ApiGateway apiGateway = new ApiGateway(getMockDeployment(), functionArn, roleArn);


        final Resource parentMockResource = PowerMock.createMock(Resource.class);
        expect(parentMockResource.getPath())
                .andReturn("/")
                .anyTimes();
        expect(parentMockResource.getParent())
                .andReturn(null)
                .anyTimes();


        PowerMock.replayAll();

        assertEquals(apiGateway.getParentPathOfResource(parentMockResource),
                null);


        final Resource mockResource = PowerMock.createMock(Resource.class);
        expect(mockResource.getPath())
                .andReturn("{id}")
                .anyTimes();

        expect(mockResource.getParent())
                .andReturn(parentMockResource)
                .anyTimes();


        final Resource mockSubResource = PowerMock.createMock(Resource.class);

        expect(mockSubResource.getPath())
                .andReturn("{id}/info")
                .anyTimes();

        expect(mockSubResource.getParent())
                .andReturn(parentMockResource)
                .anyTimes();

        PowerMock.replayAll();


        assertEquals("/",
                apiGateway.getParentPathOfResource(mockResource));


        assertEquals("/{id}",
                apiGateway.getParentPathOfResource(mockSubResource));

    }

}