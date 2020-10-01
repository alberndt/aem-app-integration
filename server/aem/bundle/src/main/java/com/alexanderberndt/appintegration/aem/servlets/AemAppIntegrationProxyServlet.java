package com.alexanderberndt.appintegration.aem.servlets;

import com.alexanderberndt.appintegration.aem.engine.AemAppIntegrationEngine;
import org.apache.commons.lang3.StringUtils;
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
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    private final AemAppIntegrationEngine engine;

    private final Pattern pathPattern = Pattern.compile("^/ext/([^/]+)/(.*)(\\?.*)?$");

    @Activate
    public AemAppIntegrationProxyServlet(@Reference AemAppIntegrationEngine engine) {
        this.engine = engine;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);

        boolean found = false;

        try {
            final String path = request.getRequestURI();
            final Matcher pathMatcher = pathPattern.matcher(path);
            if (pathMatcher.matches()) {
                final String applicationId = pathMatcher.group(1);
                final String relativePath = pathMatcher.group(2);

                final byte[] byteArray = engine.getStaticResourceAsByteArray(applicationId, relativePath);
                if (byteArray != null) {
                    found = true;
                    if (StringUtils.endsWith(relativePath, ".webp")) {
                        response.setContentType("image/webp");
                    }
                    response.getOutputStream().write(byteArray);
                } else {
                    out.println(String.format("Resource %s for application %s not found!", relativePath, applicationId));
                }
            } else {
                out.println(String.format("Path %s seems not to match!", path));
            }


            if (!found) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/plain");
                response.setCharacterEncoding("utf-8");
                response.getWriter().println(path);
                response.getWriter().println(buffer.toString());
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            try {
                e.printStackTrace(response.getWriter());
            } catch (IOException ioException) {
                LOG.error("Cannot get Writer", e);
            }
            LOG.error("Cannot get writer", e);
        }
    }
}


