package com.alexanderberndt.appintegration.examples.simple1;

import com.alexanderberndt.appintegration.api.IntegrationException;
import com.alexanderberndt.appintegration.core.CoreIntegrationTaskFactory;
import com.alexanderberndt.appintegration.core.IntegrationJobImpl;
import com.alexanderberndt.appintegration.core.parser.IntegrationJobParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SimpleExample1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExample1.class);

    private IntegrationJobImpl job;

    public static void main(String[] args) {
        SimpleExample1 simpleExample1 = new SimpleExample1();
        simpleExample1.init();
        simpleExample1.run();
    }

    private void init() {
        LOGGER.info("init");

        try {
            final InputStream importTasksInputStream = this.getClass().getResourceAsStream("simple-example-integration-1.yaml");
            assert (importTasksInputStream != null);
            String yaml = IOUtils.toString(importTasksInputStream, "utf-8");
            this.job = IntegrationJobParser.parseTaskPipelineYaml(yaml, new CoreIntegrationTaskFactory());
            assert (this.job != null);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new IntegrationException("Cannot load import tasks!", e);
        }

        // instances setup
        this.job.newInstance(createInstanceProperties("2016"));
        this.job.newInstance(createInstanceProperties("2017"));
        this.job.newInstance(createInstanceProperties("2018"));
    }

    private void run() {
        LOGGER.info("run");
        this.job.executeImport();
    }

    private Map<String, Object> createInstanceProperties(String year) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("year", year);
        return properties;
    }
}
