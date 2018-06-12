package com.alexanderberndt.aemappintegration.api;

import java.io.InputStream;

public interface RemoteResourceFetcher {

    byte[] getRemoteResource(RequestParameterMap parameterMap);

    InputStream streamRemoteResource(RequestParameterMap parameterMap);

}
