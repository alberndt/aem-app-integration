package com.alexanderberndt.appintegration.parser;

import java.util.List;

class IntegrationJobDef {

    private String applicationId;

    private String baseUrl;

    private String htmlSnippetUrl;

    private String htmlSnippetQuery;

    private List<IntegrationTaskDef> tasks;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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

    public List<IntegrationTaskDef> getTasks() {
        return tasks;
    }

    public void setTasks(List<IntegrationTaskDef> tasks) {
        this.tasks = tasks;
    }
}
