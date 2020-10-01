package com.alexanderberndt.appintegration.aem.engine.models;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.StringJoiner;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AemExternalApplicationModel  {

    @Self
    private Resource resource;

    @OSGiService
    private AemAppIntegrationEngine integrationEngine;

    private SlingApplicationInstance instance;

    @PostConstruct
    public void init() {
        this.instance = resource.adaptTo(SlingApplicationInstance.class);
    }

    public boolean hasInstance() {
        return instance != null;
    }

    public SlingApplicationInstance getInstance() {
        return instance;
    }

    public AemAppIntegrationEngine getIntegrationEngine() {
        return integrationEngine;
    }

    public String getHtmlSnippet() throws IOException {
        if (integrationEngine != null) {
            ExternalResource htmlSnippetRes = integrationEngine.getHtmlSnippet(instance);
            if (htmlSnippetRes != null) {
                try {
                    return htmlSnippetRes.getContentAsParsedObject(String.class);
                } catch (Exception e) {
                    return e.getMessage();
                }
            } else {
                return "External Resource NOT loaded!";
            }
        } else {
            return "App-Integration-Engine not found!";
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AemExternalApplicationModel.class.getSimpleName() + "[", "]")
                .add("instance=" + instance)
                .add("integrationEngine=" + integrationEngine)
                .toString();
    }
}
