package com.alexanderberndt.appintegration.core.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationFactory;
import com.alexanderberndt.appintegration.aem.engine.SlingApplicationInstance;
import com.alexanderberndt.appintegration.engine.resources.conversion.ConversionException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@Component(service = { Servlet.class })
@SlingServletPaths({"/bin/servlets/TestAlex"})
@ServiceDescription("Simple Demo Servlet")
public class TestServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AemAppIntegrationFactory appIntegrationFactory;

    @Reference
    private AemAppIntegrationEngine appIntegrationEngine;


    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        try {

            ResourceResolver resolver = req.getResourceResolver();


            out.println(new ConversionException("Hello"));

            out.println(appIntegrationFactory);
            out.println(appIntegrationFactory.getAllApplications());
            out.println(appIntegrationFactory.getAllResourceLoaders());


            Resource res = resolver.getResource("/content/aem-app-integration-demo/us/newsletter/jcr:content/par/component");

            out.println(res);
            SlingApplicationInstance instance = res.adaptTo(SlingApplicationInstance.class);
            out.println(instance);

            appIntegrationEngine.prefetch(Collections.singletonList(instance));
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }
}
