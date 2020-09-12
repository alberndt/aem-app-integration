package com.alexanderberndt.appintegration.core.servlets.impl;

import com.alexanderberndt.appintegration.aem.engine.AemExternalResourceCache;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.Servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@Component(service = { Servlet.class })
@SlingServletPaths({"/bin/servlets/TestAlex2"})
@ServiceDescription("Simple Demo Servlet")
public class TestServlet2 extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");

        PrintWriter out = resp.getWriter();
        ResourceResolver resolver = req.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        out.write("Hello world v2");

        try {
            JcrUtil.createPath("/var/aem-app-integration/my-app/files", NodeType.NT_UNSTRUCTURED, session);
            Resource rootRes = resolver.getResource("/var/aem-app-integration/my-app/files");
            AemExternalResourceCache cache = new AemExternalResourceCache(resolver, "test-app");

            ExternalResourceRef resourceRef = ExternalResourceRef.create("https://admin:admin@www.example.com:8080/context/path1/path2/helloworld.html?lang=de&country=at&time=iojasdlladsfjalsdjf#fragment", ExternalResourceType.TEXT);
            InputStream inputStream = new ByteArrayInputStream("Hello World, my dear!".getBytes());
            ExternalResource resource = new ExternalResource(inputStream, resourceRef, () -> null);


            cache.storeResource(resource);

            resolver.commit();


        } catch (RepositoryException e) {
            e.printStackTrace();
        }



    }
}
