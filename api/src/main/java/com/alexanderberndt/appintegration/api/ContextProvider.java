package com.alexanderberndt.appintegration.api;

import java.util.Map;

public interface ContextProvider {

  Map<String, Object> getContext();

}
