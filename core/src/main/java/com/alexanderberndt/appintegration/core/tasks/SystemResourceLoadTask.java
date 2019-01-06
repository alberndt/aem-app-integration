package com.alexanderberndt.appintegration.core.tasks;

import com.alexanderberndt.appintegration.api.AbstractIntegrationTask;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SystemResourceLoadTask /*extends AbstractIntegrationTask<Object, byte[]>*/ {

//    // Logger
//    private static final Logger LOGGER = LoggerFactory.getLogger(SystemResourceLoadTask.class);
//
//    public static final String TASK_NAME = "system-resource";
//
//    public static final String BASE_PATH_PARAM = "base-path";
//
//
//    private String basePath;
//
//    public SystemResourceLoadTask() {
//        super(Object.class, byte[].class);
//    }
//
//    @Override
//    protected void postSetup() {
//        this.basePath = this.getProperty(BASE_PATH_PARAM, String.class);
//    }
//
//    @Override
//    public byte[] execute(Object data, IntegrationContext context) {
//
//        byte[] loadedRes = null;
//
//        try (final InputStream in = this.getClass().getResourceAsStream(this.basePath)) {
//
//            if (in != null) {
//                final ByteArrayOutputStream tmp = new ByteArrayOutputStream();
//                IOUtils.copyLarge(in, tmp, new byte[2048]);
//                loadedRes = tmp.toByteArray();
//            } else {
//                context.addError(LOGGER, "Could not open system-resource {}", "alex");
//            }
//
//        } catch (IOException e) {
//            context.addError(LOGGER, "Could not get system-resource due to {}", e.getMessage(), e);
//        }
//
//        return loadedRes;
//    }
//

}
