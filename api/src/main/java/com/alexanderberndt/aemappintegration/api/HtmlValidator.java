package com.alexanderberndt.aemappintegration.api;

import org.jsoup.nodes.Document;

public interface HtmlValidator extends IntegrationStep {

    boolean isValid(Document doc, IntegrationContext context);
}
