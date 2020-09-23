package com.alexanderberndt.appintegration.tasks.process.html;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class ExtractHtmlSnippetTask implements ProcessingTask {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String SNIPPET_QUERY_PARAM = "snippetQuery";
    public static final String FALLBACK_SNIPPET_QUERY = "fallbackSnippetQuery";
    public static final String JS_REF_QUERY = "jsRefQuery";
    public static final String CSS_REF_QUERY = "cssRefQuery";
    public static final String MANIFEST_REF_QUERY = "manifestRefQuery";


    @Override
    public void declareTaskPropertiesAndDefaults(TaskContext taskContext) {
        taskContext.setValue(SNIPPET_QUERY_PARAM, "body *[data-app-integration]=html-snippet]");
        taskContext.setValue(FALLBACK_SNIPPET_QUERY, "body");
        taskContext.setValue(JS_REF_QUERY, "script[type=text/javascript][data-app-integration=static][src]");
        taskContext.setValue(CSS_REF_QUERY, "link[rel=stylesheet][data-app-integration=static][href]");
        taskContext.setValue(MANIFEST_REF_QUERY, "html[data-app-integration-manifest");
    }

    @Override
    public void process(@Nonnull TaskContext taskContext, @Nonnull ExternalResource resource) {

        if (resource.getType() != ExternalResourceType.HTML_SNIPPET) {
            taskContext.addWarning("Only HTML-Snippets are supported!");
            return;
        }

        final Document doc;
        try {
            doc = resource.getContentAsParsedObject(Document.class);
        } catch (IOException e) {
            LOG.error("Failed to parse html", e);
            taskContext.addError("Failed to parse html: %s", e.getMessage());
            return;
        }

        taskContext.setValue(SNIPPET_QUERY_PARAM, "body *[data-app-integration=html-snippet]");
        taskContext.setValue(FALLBACK_SNIPPET_QUERY, "body");
        taskContext.setValue(JS_REF_QUERY, "script[type=text/javascript][data-app-integration=static][src]");
        taskContext.setValue(CSS_REF_QUERY, "link[rel=stylesheet][data-app-integration=static][href]");
        taskContext.setValue(MANIFEST_REF_QUERY, "html[data-app-integration=manifest]");


        final String snippetQuery = taskContext.getValue(SNIPPET_QUERY_PARAM, String.class);
        String snippet = doc.select(snippetQuery).removeAttr("data-app-integration").outerHtml();
        if (StringUtils.isBlank(snippet)) {
            taskContext.addWarning("Failed to extract snippet");
            final String fallbackSnippetQuery = taskContext.getValue(FALLBACK_SNIPPET_QUERY, String.class);
            if (StringUtils.isNotBlank(fallbackSnippetQuery)) {
                snippet = doc.select("body").html();
                if (StringUtils.isBlank(snippet)) {
                    taskContext.addWarning("Failed to extract body");
                    snippet = doc.outerHtml();
                }
            }
        }
        resource.setContent(snippet);


        // search for javascript files
        final String jsRefQuery = taskContext.getValue(JS_REF_QUERY, String.class);
        final Elements javaScriptsList = doc.select(jsRefQuery);
        extractReferencedFiles(resource, javaScriptsList, ExternalResourceType.JAVASCRIPT, "src", "jsTags");

        // search for css files
        final String cssRefQuery = taskContext.getValue(CSS_REF_QUERY, String.class);
        final Elements stylesheetList = doc.select(cssRefQuery);
        extractReferencedFiles(resource, stylesheetList, ExternalResourceType.CSS, "href", "cssTags");

        // search for cache-manifest
        final String manifestRefQuery = taskContext.getValue(MANIFEST_REF_QUERY, String.class);
        final Elements manifestList = doc.select(manifestRefQuery);
        extractReferencedFiles(resource, manifestList, ExternalResourceType.CACHE_MANIFEST, "data-viega-manifest", "cacheManifest");
    }


    private void extractReferencedFiles(@Nonnull ExternalResource resource, @Nonnull Elements
            htmlElements, @Nonnull ExternalResourceType expectedType, String urlAttr, String metaDataProperty) {

        final List<String> tagList = new ArrayList<>();

        for (Element htmlElement : htmlElements) {
            final String url = htmlElement.attr(urlAttr);
            if (StringUtils.isNotBlank(url)) {

                resource.addReference(url, expectedType);

                htmlElement.removeAttr("data-viega-app");
//
//                final ImportJob additionalJob = job.addAdditionalResource(expectedType, url);
//                // ToDo: Rethink url-mapping
//                // final String contextPath = "/exts/" + StringUtils.substringBefore(job.getExternalProviderId(), "_");
////                final String additionalUrl;
////                if (StringUtils.startsWith(additionalJob.getFilePath(), contextPath)) {
////                    additionalUrl = additionalJob.getFilePath();
////                } else {
////                    additionalUrl = contextPath + additionalJob.getFilePath();
////                }
//
//                htmlElement.attr(urlAttr, additionalUrl);
                tagList.add(htmlElement.outerHtml());
            }
        }
        resource.setMetadata(metaDataProperty, tagList.toArray());
    }
}
