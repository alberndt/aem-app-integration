package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import com.day.cq.commons.jcr.JcrUtil;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
class AemExternalResourceCacheTest {

    private final AemContext context = new AemContext();

    @Nonnull
    protected ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, Map<String, Object> metadataMap) {
        return new ExternalResource(uri, type, content, metadataMap, () -> Collections.singletonList(new StringConverter()));
    }


    @Test
    void storeResource() throws URISyntaxException {
        final AemExternalResourceCache cache = new AemExternalResourceCache(context.resourceResolver(), "test-app");
        final Resource rootRes = context.create().resource("/var/aem-app-integration");


        final ExternalResourceRef resourceRef = new ExternalResourceRef(new URI("http://www.example.com/txt/hello-world.txt"), ExternalResourceType.TEXT);
        final InputStream inputStream = new ByteArrayInputStream("Hello World!".getBytes());

        cache.startLongRunningWrite("v1");
        cache.storeResource(new ExternalResource(inputStream, resourceRef, () -> null));
        cache.commitLongRunningWrite();
        dumpResource(rootRes);

        ExternalResource cachedRes = cache.getCachedResource(resourceRef.getUri(), this::createExternalResource);
        assertNotNull(cachedRes);
    }

    private void dumpResource(Resource resource) {
        System.out.println(resource.getPath());
        ValueMap valueMap = resource.getValueMap();
        System.out.println(valueMap.entrySet());
        for (Resource childRes : resource.getChildren()) {
            dumpResource(childRes);
        }
    }

    @Test
    void getCachePath() throws URISyntaxException {

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