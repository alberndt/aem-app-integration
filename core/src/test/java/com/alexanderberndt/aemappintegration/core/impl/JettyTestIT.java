package com.alexanderberndt.aemappintegration.core.impl;

import org.eclipse.jetty.server.Server;
import org.junit.Test;

public class JettyTestIT {


    @Test
    public void testAll() throws Exception {

//        Server server = new Server(8080);
//        server.setHandler(new HelloHandler());
//
//        server.start();
//
        HttpDownloadTask task = new HttpDownloadTask();
        task.execute();

        Thread.sleep(8000);
//
//
//
//
//
//
//
//
//        server.stop();

    }



}
