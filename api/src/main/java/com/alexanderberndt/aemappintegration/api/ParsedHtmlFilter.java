package com.alexanderberndt.aemappintegration.api;

import org.jsoup.nodes.Document;

public interface ParsedHtmlFilter {

    void filter(Document doc);

}
