package com.alexanderberndt.appintegration.aem.engine;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import javax.jcr.RepositoryException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AemExternalResourceCacheTest {

    @Test
    void storeResource() {
    }

    @Test
    void getCachedResource() {
    }

    @Test
    void getCachePath() throws URISyntaxException, RepositoryException {

        //Node nodeMock = Mockito.mock(Node.class);

        URI uri = new URI("https://admin:admin@www.example.com:8080/context/path1/path1/helloworld.html?lang=de&country=at&time=iojasdlladsfjalsdjf#fragment");
        assertEquals("/context/path1/path1/helloworld.html", uri.getPath());

        String[] splitPath = StringUtils.splitByWholeSeparator(uri.getPath(), "/");
        for (String pathElem : splitPath) {
            System.out.println(JcrUtil.createValidName(pathElem));
        }



        URI uri2 = new URI("adff://apps/alexb");
        System.out.println(uri2);

        URI uri3 = uri2.resolve("more");
        System.out.println(uri3);


    }
}