package com.alexanderberndt.appintegration.pipeline.builder;

import com.alexanderberndt.appintegration.utils.DataMap;

public interface TaskDefinition {

    DataMap getConfiguration();

    String getName();

    String getFilter();

    String getFileTypes();
}
