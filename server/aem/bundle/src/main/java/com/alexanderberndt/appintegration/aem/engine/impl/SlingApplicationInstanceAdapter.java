package com.alexanderberndt.appintegration.aem.engine.impl;

//@Component(
//        property = {
//                AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource",
//                AdapterFactory.ADAPTER_CLASSES + "=com.alexanderberndt.appintegration.aem.engine.model.SlingApplicationInstance"
//        })
public class SlingApplicationInstanceAdapter /* implements AdapterFactory */{

//    @Override
//    @SuppressWarnings("unchecked")
//    public <T> T getAdapter(@Nonnull Object adaptable, @Nonnull Class<T> type) {
//        if (adaptable instanceof Resource) {
//            final Resource resource = (Resource) adaptable;
//            final ValueMap valueMap = resource.getValueMap();
//
//            final String applicationId = valueMap.get(
//                    "application", String.class);
//            final String componentId = valueMap.get("component", String.class);
//
//            if (StringUtils.isNoneBlank(applicationId, componentId)) {
//                return (T) new SlingApplicationInstance(resource, applicationId, componentId);
//            }
//        }
//        // cannot adapt
//        return null;
//    }
}

