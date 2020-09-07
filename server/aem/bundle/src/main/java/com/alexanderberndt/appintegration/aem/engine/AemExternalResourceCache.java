package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

import java.net.URI;

public class AemExternalResourceCache {


    public void storeResource(ExternalResource resource) {

    }

    public void getCachedResource(ExternalResourceRef resourceRef) {

    }

    public String[] getCachePath(URI uri) {
        return null;


//        //Node nodeMock = Mockito.mock(Node.class);
//
//        URI uri = new URI("https://admin:admin@www.example.com:8080/context/path1/path1/helloworld.html?lang=de&country=at&time=iojasdlladsfjalsdjf#fragment");
//        assertEquals("/context/path1/path1/helloworld.html", uri.getPath());
//
//        String[] splitPath = StringUtils.splitByWholeSeparator(uri.getPath(), "/");
//        for (String pathElem : splitPath) {
//            System.out.println(JcrUtil.createValidName(pathElem));
//        }

    }


}
