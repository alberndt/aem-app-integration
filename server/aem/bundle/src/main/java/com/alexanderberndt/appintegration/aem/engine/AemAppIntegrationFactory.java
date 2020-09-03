package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.*;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.*;

@Component(service = AemAppIntegrationFactory.class)
public class AemAppIntegrationFactory implements AppIntegrationFactory<SlingApplicationInstance, AemGlobalContext> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, AemApplication> applicationMap = new HashMap<>();

    private final Map<String, ResourceLoader> resourceLoaderMap = new HashMap<>();

    private final Map<String, AemContextProvider> contextProviderMap = new HashMap<>();

    @Reference
    private AemProcessingPipelineFactory processingPipelineFactory;

    private final List<TextParser> textParserList= new ArrayList<>();


    @Nonnull
    @Override
    public Map<String, Application> getAllApplications() {
        return Collections.unmodifiableMap(applicationMap);
    }

    @Nonnull
    @Override
    public Map<String, ResourceLoader> getAllResourceLoaders() {
        return Collections.unmodifiableMap(resourceLoaderMap);
    }

    @Nonnull
    @Override
    public Map<String, ContextProvider<SlingApplicationInstance>> getAllContextProvider() {
        return Collections.unmodifiableMap(contextProviderMap);
    }

    @Nonnull
    @Override
    public ProcessingPipelineFactory<AemGlobalContext> getProcessingPipelineFactory() {
        return processingPipelineFactory;
    }

    @Nonnull
    @Override
    public Collection<TextParser> getAllTextParsers() {
        return textParserList;
    }


    @Nullable
    @Override
    public Application getApplication(@Nonnull String id) {
        return applicationMap.get(id);
    }

    @Nullable
    @Override
    public ResourceLoader getResourceLoader(String id) {
        return resourceLoaderMap.get(id);
    }

    @Nullable
    @Override
    public ContextProvider<SlingApplicationInstance> getContextProvider(@Nonnull String providerName) {
        return contextProviderMap.get(providerName);
    }

    @Reference(name = "application", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindApplication(final AemApplication application, Map<String,?> ref) {
        LOG.info("bindApplication {} with ref {}", application, ref);
        applicationMap.put(application.getApplicationId(), application);
    }

    protected void unbindApplication(final AemApplication application) {
        applicationMap.remove(application.getApplicationId());
    }

    @Reference(name = "resourceLoader", cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindResourceLoader(final ResourceLoader resourceLoader) {
        resourceLoaderMap.put(resourceLoader.getClass().getSimpleName(), resourceLoader);
    }

    protected void unbindResourceLoader(final ResourceLoader resourceLoader) {
        resourceLoaderMap.remove(resourceLoader.getClass().getSimpleName());
    }

    @Reference(name = "contextProvider", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindContextProvider(final AemContextProvider contextProvider) {
        contextProviderMap.put(contextProvider.getClass().getSimpleName(), contextProvider);
    }

    protected void unbindContextProvider(final AemContextProvider contextProvider) {
        contextProviderMap.remove(contextProvider.getClass().getSimpleName());
    }

    @Reference(name = "textParser", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindTextParser(final TextParser textParser) {
        textParserList.add(textParser);
    }

    protected void unbindTextParser(final TextParser textParser) {
        textParserList.remove(textParser);
    }
}
