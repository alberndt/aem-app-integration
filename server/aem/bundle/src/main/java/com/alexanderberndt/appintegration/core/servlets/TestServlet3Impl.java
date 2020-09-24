package com.alexanderberndt.appintegration.core.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import com.alexanderberndt.appintegration.aem.engine.model.SlingApplicationInstance;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component(service = {Servlet.class})
@SlingServletPaths({"/bin/servlets/TestAlex3"})
@ServiceDescription("Simple Demo Servlet")
public class TestServlet3Impl extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Reference
    private transient AemAppIntegrationEngine integrationEngine;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        LOG.info("Servlet");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            out.println("Hello world v3");
            final ResourceResolver resolver = request.getResourceResolver();

            final List<String> resourceTypesList = Arrays.asList("aem-app-integration/components/component", "unknown");
            final String resourceTypesSet = resourceTypesList.stream().map(rt -> "'" + rt + "'").collect(Collectors.joining(","));
            final String query = "SELECT * FROM [nt:base] WHERE [sling:resourceType] IN (" + resourceTypesSet + ") AND ISDESCENDANTNODE('/content')";

            final List<SlingApplicationInstance> instanceList = new ArrayList<>();
            final Iterator<Resource> iter = resolver.findResources(query, Query.JCR_SQL2);
            while (iter.hasNext()) {
                final Resource instanceRes = iter.next();
                SlingApplicationInstance instance = instanceRes.adaptTo(SlingApplicationInstance.class);
                if (instance != null) instanceList.add(instance);
            }

            integrationEngine.prefetch(instanceList);
            LOG.info("done");

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }
}
