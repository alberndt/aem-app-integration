package com.alexanderberndt.appintegration.core.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogger;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.google.common.io.LineReader;
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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;

@Component(service = { Servlet.class })
@SlingServletPaths({"/bin/servlets/TestAlex3"})
@ServiceDescription("Simple Demo Servlet")
public class TestServlet3Impl extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Reference
    private AemAppIntegrationEngine integrationEngine;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {

        LOG.info("Servlet");
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        try {

            out.write("Hello world v3");


            String path = "/apps/aem-app-integration/clientlibs/clientlib-grid/less/grid.less";
            ResourceResolver resolver = req.getResourceResolver();

            Resource res = resolver.getResource(path);
            out.println(res);

            InputStream in = res.adaptTo(InputStream.class);
            out.println(res);


            if (in != null) {

                LineReader lr = new LineReader(new InputStreamReader(in));
                String lastLine;
                while ((lastLine = lr.readLine()) != null) {
                    out.println(lastLine);
                }

            }

            LOG.info("Create Log-Appender");

            LogAppender appender = integrationEngine.createLogAppender(resolver, "test-app");
            IntegrationLogger logger = new IntegrationLogger(appender);
            logger.addWarning("something went wrong with %s!", "test-servlet");

            ResourceLogger resourceLogger = logger.createResourceLogger("application-info.json");
            resourceLogger.setStatus(LogStatus.FAILED);
            resourceLogger.setSize("100kb");
            resourceLogger.setLoadStatus("loaded");

            LOG.info("Before commit");

            resolver.commit();
            LOG.info("done");

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }
}
