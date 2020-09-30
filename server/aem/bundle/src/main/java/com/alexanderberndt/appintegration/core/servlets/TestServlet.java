package com.alexanderberndt.appintegration.core.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationFactory;
import com.alexanderberndt.appintegration.aem.engine.models.SlingApplicationInstance;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.annotation.Nonnull;
import javax.jcr.query.Query;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Component(service = {Servlet.class})
@SlingServletPaths({"/bin/servlets/TestAlex"})
@ServiceDescription("Simple Demo Servlet")
public class TestServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AemAppIntegrationFactory appIntegrationFactory;

    @Reference
    private AemAppIntegrationEngine appIntegrationEngine;


    @Override
    protected void doGet(@Nonnull final SlingHttpServletRequest request, @Nonnull final SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        try {
            for (Map.Entry<String, ResourceLoader> resourceLoaderEntry : appIntegrationFactory.getAllResourceLoaders().entrySet()) {
                out.println(String.format("%15s: %s", resourceLoaderEntry.getKey(), resourceLoaderEntry.getValue().getClass().getName()));
            }

            for (Map.Entry<String, Application> applicationEntry : appIntegrationFactory.getAllApplications().entrySet()) {
                out.println(applicationEntry.getKey());
                out.println("=====================================");
                Application application = applicationEntry.getValue();
                out.println("application id: " + application.getApplicationId());
                out.println("application-info url: " + application.getApplicationInfoUrl());
                out.println("resource loader: " + application.getResourceLoaderName());
                out.println("context providers: " + application.getContextProviderNames());
                out.println("processing pipeline: " + application.getProcessingPipelineName());
                out.println();
                out.println();
            }


            out.println("query instances");
            out.println("=====================================");

            final ResourceResolver resolver = request.getResourceResolver();

            final List<String> resourceTypesList = Arrays.asList("aem-app-integration/components/component", "unknown");
            final String resourceTypesSet = resourceTypesList.stream().map(rt -> "'" + rt + "'").collect(Collectors.joining(","));
            final String query = "SELECT * FROM [nt:base] WHERE [sling:resourceType] IN (" + resourceTypesSet + ") AND ISDESCENDANTNODE('/content')";

            final List<SlingApplicationInstance> instanceList = new ArrayList<>();
            final Iterator<Resource> iter = resolver.findResources(query, Query.JCR_SQL2);
            while (iter.hasNext()) {
                final Resource instanceRes = iter.next();
                SlingApplicationInstance instance = instanceRes.adaptTo(SlingApplicationInstance.class);
                out.println(String.format("%-90s ==> %s", instanceRes.getPath(), instance));
                if (instance != null) instanceList.add(instance);
            }


            out.println();
            out.println();
            out.println("prefetch");
            out.println("=====================================");
            appIntegrationEngine.prefetch(instanceList);

            out.println("done");


        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }
}
