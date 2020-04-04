package com.alexanderberndt.testapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        final PrintWriter out = resp.getWriter();

        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dataMap = new HashMap<>();

//        dataMap.put("auth-type", req.getAuthType());
        dataMap.put("contextPath", req.getContextPath());
//        dataMap.put("cookies", req.getCookies());
//
        final Enumeration<String> headerEnum = req.getHeaderNames();
        while (headerEnum.hasMoreElements()) {
            String header = headerEnum.nextElement();
            dataMap.put("header-" + header, req.getHeader(header));
        }

        dataMap.put("parameter-map", req.getParameterMap());

        dataMap.put("path-info", req.getPathInfo());
        dataMap.put("path-translated", req.getPathTranslated());

//        dataMap.put("parts", req.getParts());


        dataMap.put("query-string", req.getQueryString());
        dataMap.put("method", req.getMethod());

        dataMap.put("servlet-path", req.getServletPath());
        dataMap.put("content-type", req.getContentType());

        dataMap.put("request-uri", req.getRequestURI());
        dataMap.put("request-url", req.getRequestURL());

        dataMap.put("local-addr", req.getLocalAddr());
        dataMap.put("locale", req.getLocale());
        dataMap.put("local-name", req.getLocalName());
        dataMap.put("local-port", req.getLocalPort());

        dataMap.put("remote-addr", req.getRemoteAddr());
        dataMap.put("remote-user", req.getRemoteUser());
        dataMap.put("remote-host", req.getRemoteHost());
        dataMap.put("remote-port", req.getRemotePort());

        objectMapper.writeValue(out, dataMap);

        out.println("Hello World by Alex B. (2.0)");
    }
}
