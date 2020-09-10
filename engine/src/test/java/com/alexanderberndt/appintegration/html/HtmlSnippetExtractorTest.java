package com.alexanderberndt.appintegration.html;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HtmlSnippetExtractorTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void verifyConfig() {
    }

    @Test
    void validateValidConfiguration() {
//        Configuration configuration = new BaseConfiguration();
//        configuration.addProperty("query", "div.myClass[attr]");
//
//        HtmlSnippetExtractor extractor = new HtmlSnippetExtractor();

//        IntegrationStepResult<String> result = extractor.validateConfiguration(configuration);
//        assertNotNull(result);
//        assertEquals(result.getStatus(), IntegrationStepResult.Status.OK);
//        assertEquals(result.getResult(), "Valid configuration");
    }

    @Test
    void validateInvalidConfiguration1() {
//        Configuration configuration = new BaseConfiguration();
//
//        HtmlSnippetExtractor extractor = new HtmlSnippetExtractor();
//
//        IntegrationStepResult<String> result = extractor.validateConfiguration(configuration);
//        assertNotNull(result);
//        assertEquals(result.getStatus(), IntegrationStepResult.Status.ERROR);
//        assertEquals(result.getResult(), "Incomplete configuration");
//        assertEquals(IntegrationMessage.Type.ERROR, result.getMessageList().get(0).getType());
//        assertTrue(result.getMessageList().get(0).getMessage().startsWith("Missing config-parameter "));
    }

    @Test
    void validateInvalidConfiguration2() {
//        Configuration configuration = new BaseConfiguration();
//        configuration.addProperty("query", "//alex");
//
//        HtmlSnippetExtractor extractor = new HtmlSnippetExtractor();

//        IntegrationStepResult<String> result = extractor.validateConfiguration(configuration);
//        assertNotNull(result);
//        assertEquals(result.getStatus(), IntegrationStepResult.Status.ERROR);
//        assertEquals(result.getResult(), "Wrong configuration");
//        assertEquals(1, result.getMessageList().size());
//        assertEquals(IntegrationMessage.Type.ERROR, result.getMessageList().get(0).getType());
//        assertTrue(result.getMessageList().get(0).getMessage().startsWith("Cannot parse "));
    }

    @Test
    void extractHtmlSnippet() {
    }
}