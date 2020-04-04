package com.alexanderberndt.appintegration.engine.loader;

import java.io.IOException;

public interface ResourceLoader {

    <T> T load(String url, Class<T> tClass) throws IOException;

    String resolveRelativeUrl(String baseUrl, String relativeUrl);

}
