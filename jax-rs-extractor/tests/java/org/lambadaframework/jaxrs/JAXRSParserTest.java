package org.lambadaframework.jaxrs;


import org.glassfish.jersey.server.model.Resource;
import org.junit.Test;
import org.lambadaframework.stubs.StubHandler;

import java.util.List;
import java.util.jar.JarFile;

import static org.junit.Assert.*;


public class JAXRSParserTest {

    /**
     * To run this test, lambada-stub-handlers module should be compiled
     * and packaged in lambada-stub-handlers/target folder
     * @throws Exception
     */
    @Test
    public void testScanJar() throws Exception {
        JAXRSParser parser = new JAXRSParser().withJarFile(
                "../lambada-stub-handlers/target/lambada-stub-handlers-0.0.1.jar",
                "org.lambadaframework");
        List<Resource> resourceList = parser.scan();
        assertTrue(resourceList.size() > 0);
    }

    @Test
    public void testScanPackage() throws Exception {
        JAXRSParser parser = new JAXRSParser().withPackageName("org.lambadaframework", StubHandler.class);
        List<Resource> resourceList = parser.scan();
        assertEquals(5, resourceList.size());
        int totalMethod = 0;
        for (Resource resource : resourceList) {
            totalMethod += resource.getResourceMethods().size();
        }
        assertEquals(7, totalMethod);
    }
}