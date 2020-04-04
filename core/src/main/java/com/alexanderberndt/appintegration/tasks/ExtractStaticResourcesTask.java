package com.alexanderberndt.appintegration.tasks;

import com.alexanderberndt.appintegration.api.AppIntegrationException;
import com.alexanderberndt.appintegration.engine.processors.html.api.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractStaticResourcesTask extends AbstractIntegrationTask {

    public static final String TASK_NAME = "extract-static-resources";

    private static final String QUERY_PARAM = "query";
    private static final String ATTRIBUTE_PARAM = "attribute";
    private static final String EXPECTED_TYPE_PARAM = "expectedType";

    private IntegrationResourceType expectedResourceType;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractStaticResourcesTask.class);

    public ExtractStaticResourcesTask() {
        super(QUERY_PARAM, ATTRIBUTE_PARAM, EXPECTED_TYPE_PARAM);
    }

    @Override
    public void postSetup() {
        final String expected = this.getProperty(EXPECTED_TYPE_PARAM, String.class);
        try {
            this.expectedResourceType = IntegrationResourceType.parse(expected);
        } catch (IllegalArgumentException e) {
            throw new AppIntegrationException("Illegal " + EXPECTED_TYPE_PARAM + ": " + expected);
        }
    }

    @Override
    public void execute(IntegrationResource resource, IntegrationJob job) {

        LOGGER.info("extract static resources");

        if (!resource.isHtmlDocument()) {
            LOGGER.warn("Resource {} is not text data!", resource);
            job.addWarning("Resource {} is not a html document!", resource);
            return;
        }


        final String query = this.getProperty(QUERY_PARAM, String.class);
        final String referenceAttribute = this.getProperty(ATTRIBUTE_PARAM, String.class);

        final Elements htmlElements = resource.getDataAsHtmlDocument().select(query);

        for (Element htmlElement : htmlElements) {
            final String reference = htmlElement.attr(referenceAttribute);
            LOGGER.info("additional resource {}", reference);
            if (StringUtils.isNotBlank(reference)) {
                LOGGER.info("additional resource {} of type {}", reference, expectedResourceType);

                // ToDo: Add additional resource

                // ToDo: Path mapping

            }
        }
    }
}
