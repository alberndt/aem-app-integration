package com.alexanderberndt.appintegration.engine.processors.html.api;

import java.util.List;
import java.util.Map;

public interface IntegrationTask {

    void setApplicableResourceTypes(List<IntegrationResourceType> applicableResourceTypes);

    List<IntegrationResourceType> getApplicableResourceTypes();

    void setupTask(final Map<String, Object> properties);

    void execute(IntegrationResource resource, IntegrationJob job);

}
