package com.alexanderberndt.testapp;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class PdfDownloadServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/pdf");

        //Content-Disposition: inline
        //Content-Disposition: attachment
        //Content-Disposition: attachment; filename="filename.jpg"

//        resp.addHeader("Content-Disposition", "inline");

        resp.addHeader("Content-Disposition", "attachment; filename=\"test.pdf\"");


        InputStream in = this.getClass().getResourceAsStream("/test.pdf");
        IOUtils.copy(in, resp.getOutputStream());
    }
}
