package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.*;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Component(service = AemAppIntegrationFactory.class)
public class AemAppIntegrationFactory implements AppIntegrationFactory<SlingApplicationInstance> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, AemApplication> applicationMap = new HashMap<>();

    @Reference(name = "application", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindApplication(final AemApplication application, Map<String,?> ref) {
        LOG.info("bindApplication {} with ref {}", application, ref);
        applicationMap.put(application.getApplicationId(), application);
    }

    protected void unbindApplication(final AemApplication application) {
        applicationMap.remove(application.getApplicationId());
    }

    @Nonnull
    @Override
    public Map<String, Application> getAllApplications() {
        return Collections.unmodifiableMap(applicationMap);
    }

    @Nullable
    @Override
    public Application getApplication(@Nonnull String id) {
        return applicationMap.get(id);
    }

    private final Map<String, ResourceLoader> resourceLoaderMap = new HashMap<>();

    @Reference(name = "resourceLoader", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindResourceLoader(final ResourceLoader resourceLoader) {
        resourceLoaderMap.put(resourceLoader.getClass().getSimpleName(), resourceLoader);
    }

    protected void unbindResourceLoader(final ResourceLoader resourceLoader) {
        resourceLoaderMap.remove(resourceLoader.getClass().getSimpleName());
    }



    @Nullable
    @Override
    public ResourceLoader getResourceLoader(String id) {
        return resourceLoaderMap.get(id);
    }

    @Override
    public Map<String, ResourceLoader> getAllResourceLoaders() {
        return Collections.unmodifiableMap(resourceLoaderMap);
    }

    @Nonnull
    @Override
    public ProcessingPipelineFactory getProcessingPipelineFactory() {
        return null;
    }

    @Nullable
    @Override
    public ContextProvider<SlingApplicationInstance> getContextProvider(@Nonnull String providerName) {
        return null;
    }

    @Nonnull
    @Override
    public Collection<TextParser> getAllTextParsers() {
        return null;
    }
}
