package org.lambadaframework.jaxrs;


import org.glassfish.jersey.server.model.Resource;
import org.junit.Test;

import java.util.List;
import java.util.jar.JarFile;

import static org.junit.Assert.*;


public class JAXRSParserTest {


    @Test
    public void testScanJar() throws Exception {
        JAXRSParser parser = new JAXRSParser().withJarFile(
                "/data/work/lambada-boilerplate/target/boilerplate-0.0.1.jar",
                "org.lambadaframework.example");
        List<Resource> resourceList = parser.scan();
        assertTrue(resourceList.size() > 0);
    }

    @Test
    public void testScanPackage() throws Exception {
        JAXRSParser parser = new JAXRSParser().withPackageName("org.lambadaframework", JAXRSParser.class);
        List<Resource> resourceList = parser.scan();
        assertEquals(3, resourceList.size());

        int totalMethod = 0;
        for (Resource resource : resourceList) {
            totalMethod += resource.getResourceMethods().size();
        }
        assertEquals(5, totalMethod);
    }
}