package com.alexanderberndt.appintegration.core.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.resources.conversion.ConversionException;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

@Component(service = { Servlet.class })
@SlingServletPaths({"/bin/servlets/TestAlex"})
@ServiceDescription("Simple Demo Servlet")
public class TestServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AemAppIntegrationFactory appIntegrationFactory;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write("Hello world");

        PrintWriter out = resp.getWriter();

        out.println(new ConversionException("Hello"));

        out.println(appIntegrationFactory);
        out.println(appIntegrationFactory.getAllApplications());
    }
}
