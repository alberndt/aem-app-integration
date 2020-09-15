package com.alexanderberndt.appintegration.aem.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;


@Component(
        service = Servlet.class,
        scope = ServiceScope.PROTOTYPE,
        property = {
                "osgi.http.whiteboard.servlet.pattern=/ext/*",
                "osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=org.apache.sling)"}
)
public class AemAppIntegrationProxyServlet extends HttpServlet {

    private static final long serialVersionUID = -1812227983368047424L;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AemAppIntegrationEngine integrationEngine;

    @Activate
    public AemAppIntegrationProxyServlet(@Reference AemAppIntegrationEngine integrationEngine) {
        this.integrationEngine = integrationEngine;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        try {
            PrintWriter out = response.getWriter();
            out.println("Hello World!");
            out.println(integrationEngine);
        } catch (IOException e) {
            LOG.error("Cannot get writer", e);
        }
    }
}


