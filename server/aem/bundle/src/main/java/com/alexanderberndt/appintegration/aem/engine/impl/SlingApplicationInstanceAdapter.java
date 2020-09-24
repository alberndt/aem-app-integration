package com.alexanderberndt.appintegration.aem.engine.impl;

import com.alexanderberndt.appintegration.aem.engine.model.SlingApplicationInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;


@Component(
        property = {
                AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource",
                AdapterFactory.ADAPTER_CLASSES + "=com.alexanderberndt.appintegration.aem.engine.model.SlingApplicationInstance"
        })
public class SlingApplicationInstanceAdapter implements AdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter(@Nonnull Object adaptable, @Nonnull Class<T> type) {
        if (adaptable instanceof Resource) {
            final Resource resource = (Resource) adaptable;
            final ValueMap valueMap = resource.getValueMap();

            final String applicationId = valueMap.get("application", String.class);
            final String componentId = valueMap.get("component", String.class);

            if (StringUtils.isNoneBlank(applicationId, componentId)) {
                return (T) new SlingApplicationInstance(resource, applicationId, componentId);
            }
        }
        // cannot adapt
        return null;
    }
}

