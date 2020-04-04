package com.alexanderberndt.appintegration.engine.loader.impl;

import org.apache.commons.lang3.StringUtils;

public class SystemResourceLoader extends AbstractResourceLoader {

    @Override
    protected AttributedInputStream loadInternal(String url) {
        return new AttributedInputStream(ClassLoader.getSystemResourceAsStream(url));
    }

    @Override
    public String resolveRelativeUrl(String baseUrl, String relativeUrl) {
        if (StringUtils.contains(baseUrl, '/')) {
            return StringUtils.substringBeforeLast(baseUrl, "/") + relativeUrl;
        } else {
            return relativeUrl;
        }
    }
}
