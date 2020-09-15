package com.alexanderberndt.appintegration.aem.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component(
        service = Servlet.class,
        scope = ServiceScope.PROTOTYPE,
        property = {
                "osgi.http.whiteboard.servlet.pattern=/ext/*",
                "osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=org.apache.sling)"}
)
public class AemAppIntegrationProxyServlet extends HttpServlet {

    private static final long serialVersionUID = -1812227983368047424L;

    @Reference
    private AemAppIntegrationEngine integrationEngine;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");

        PrintWriter out = response.getWriter();

        out.println("Hello World!");

    }


}


