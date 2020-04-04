package com.alexanderberndt.appintegration.engine.processors.info;

import com.alexanderberndt.appintegration.api.ApplicationInfo;
import com.alexanderberndt.appintegration.engine.loader.ResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.impl.SystemResourceLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationInfoLoaderTest {

    @Test
    void load() throws IOException {
        final ResourceLoader resourceLoader = new SystemResourceLoader();
        final ApplicationInfoLoader applicationInfoLoader = new ApplicationInfoLoader();

        final ApplicationInfo info = applicationInfoLoader.load(resourceLoader, "simple-app1/server/application-info.json");

        assertNotNull(info);
        assertEquals("Newsletter", info.getName());
        assertEquals("1.0.0", info.getVersion());
        assertNotNull(info.getComponents());
        assertEquals(2, info.getComponents().size());
        assertNotNull(info.getComponents().get("subscribe"));
        assertEquals("Subscribe Newsletter", info.getComponents().get("subscribe").getName());

        System.out.println(info.debugString());

    }
}