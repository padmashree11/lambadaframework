package org.lambadaframework.jaxrs;


import org.junit.Test;
import org.lambadaframework.jaxrs.model.Resource;
import org.lambadaframework.stubs.StubHandler;

import java.util.List;

import static org.junit.Assert.*;


public class JAXRSParserTest {

    /**
     * To run this test, lambada-stub-handlers module should be compiled
     * and packaged in lambada-stub-handlers/target folder
     *
     * @throws Exception
     */
    @Test
    public void testScanJar() throws Exception {
        JAXRSParser parser = new JAXRSParser().withJarFile(
                "../stub-handlers/target/stub-handlers-0.0.5.jar",
                "org.lambadaframework");
        List<Resource> resourceList = parser.scan();
        assertTrue(resourceList.size() > 0);
    }

    @Test
    public void testScanPackage() throws Exception {
        JAXRSParser parser = new JAXRSParser().withPackageName("org.lambadaframework", StubHandler.class);
        List<Resource> resourceList = parser.scan();
        assertEquals(4, resourceList.size());
        int totalMethod = 0;
        for (Resource resource : resourceList) {
            totalMethod += resource.getResourceMethods().size();
        }
        assertEquals(6, totalMethod);

        assertEquals("/resource1", resourceList.get(1).getPath());
        assertEquals("/resource1/{id}", resourceList.get(2).getPath());
        assertEquals("/resource1/{id}/users", resourceList.get(3).getPath());
    }
}