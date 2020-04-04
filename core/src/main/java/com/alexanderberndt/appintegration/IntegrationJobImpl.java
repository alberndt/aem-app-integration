package com.alexanderberndt.appintegration;

import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationJob;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationResource;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationResourceType;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationTask;
import com.alexanderberndt.appintegration.utils.HttpDownloadUtil;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntegrationJobImpl implements IntegrationJob {

    private static Logger LOGGER = LoggerFactory.getLogger(IntegrationJobImpl.class);

    private final String applicationId;

    private URI baseURI;

    private String htmlSnippetUrl;

    private String htmlSnippetQuery;

    private List<IntegrationInstance> integrationInstanceList = new ArrayList<>();

    private List<IntegrationTask> integrationTaskList = new ArrayList<>();


    public IntegrationJobImpl(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public void executeImport() {

        for (IntegrationInstance instance : integrationInstanceList) {

            // load html page
            final String ref = resolveInstanceReference(instance);
            final IntegrationResource htmlRes = HttpDownloadUtil.download(baseURI, ref, IntegrationResourceType.HTML);

            // execute tasks
            executeTasks(htmlRes);

            // extract html snippet
            Elements htmlSnippetElements = htmlRes.getDataAsHtmlDocument().select(htmlSnippetQuery);
            final IntegrationResource htmlSnippetRes = IntegrationResourceImpl.create(IntegrationResourceType.HTML_SNIPPET, htmlSnippetElements.outerHtml());

            // execute tasks
            executeTasks(htmlSnippetRes);
        }
    }


    public IntegrationInstance newInstance(Map<String, Object> instanceProperties) {
        final IntegrationInstance newIntegrationInstance = new IntegrationInstance(this, instanceProperties);
        integrationInstanceList.add(newIntegrationInstance);
        return newIntegrationInstance;
    }

    public void addIntegrationTask(IntegrationTask task) {
        this.integrationTaskList.add(task);
    }

    @Override
    public void addWarning(Logger logger, String messagePattern, Object... objects) {
    }


    @Override
    public void addWarning(String messagePattern, Object... objects) {


    }

    @Override
    public void addWarning(String text) {

    }

    @Override
    public void addError(String messagePattern, Object... objects) {

    }

    @Override
    public void addError(Logger logger, String messagePattern, Object... objects) {

    }

    private String resolveInstanceReference(IntegrationInstance instance) {
        // resolve instance reference
        StrSubstitutor subst = new StrSubstitutor(instance.getInstanceProperties());
        return subst.replace(htmlSnippetUrl);
    }


    private void executeTasks(IntegrationResource resource) {
        for (IntegrationTask task : this.integrationTaskList) {
            if (task.getApplicableResourceTypes().contains(resource.getType())) {
                task.execute(resource, this);
            }
        }
    }

    public void setBaseURI(String url) throws MalformedURLException, URISyntaxException {
        URL baseUrl = new URL(url);
        baseURI = baseUrl.toURI();
    }


    public String getApplicationId() {
        return applicationId;
    }

    public URI getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
    }

    public String getHtmlSnippetUrl() {
        return htmlSnippetUrl;
    }

    public void setHtmlSnippetUrl(String htmlSnippetUrl) {
        this.htmlSnippetUrl = htmlSnippetUrl;
    }

    public String getHtmlSnippetQuery() {
        return htmlSnippetQuery;
    }

    public void setHtmlSnippetQuery(String htmlSnippetQuery) {
        this.htmlSnippetQuery = htmlSnippetQuery;
    }

    public List<IntegrationInstance> getIntegrationInstanceList() {
        return integrationInstanceList;
    }

    public void setIntegrationInstanceList(List<IntegrationInstance> integrationInstanceList) {
        this.integrationInstanceList = integrationInstanceList;
    }
}
