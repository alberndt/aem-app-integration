package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.aem.engine.logging.AemLogAppender;
import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.function.Function;

import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

@Component(service = AemAppIntegrationEngine.class)
public class AemAppIntegrationEngine extends AppIntegrationEngine<SlingApplicationInstance, AemGlobalContext> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String SUB_SERVICE_ID = "engine";

    @Reference
    private AemAppIntegrationFactory factory;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Nonnull
    @Override
    protected AppIntegrationFactory<SlingApplicationInstance, AemGlobalContext> getFactory() {
        return factory;
    }

    @Override
    protected <R> R callWithGlobalContext(String applicationId, Function<AemGlobalContext, R> function) {

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(SUBSERVICE, SUB_SERVICE_ID))) {

            final AemGlobalContext context = new AemGlobalContext(resolver, createLogAppender(resolver, applicationId));

            final R result = function.apply(context);

            resolver.commit();

            return result;

        } catch (LoginException | PersistenceException e) {
            throw new AppIntegrationException("Cannot login to service user session!", e);
        }
    }

    @Override
    protected <R> R callWithExternalResourceCache(String applicationId, Function<ExternalResourceCache, R> function) {
        return null;
    }



    public LogAppender createLogAppender(@Nonnull ResourceResolver resolver, @Nonnull String applicationId) throws PersistenceException {

        final GregorianCalendar now = new GregorianCalendar();
        final String rootPath = String.format("/var/aem-app-integration/logs/%1$s/%2$TY/%2$Tm/%2$Td/%2$TH/%2$TM", applicationId, now);

        LOG.debug("Create Log-Root {}", rootPath);
        final Resource rootLoggingRes = ResourceUtil.getOrCreateResource(
                resolver, rootPath, "alex/resourcetype", "alex/intermediate", true);

        final String logResName = ResourceUtil.createUniqueChildName(rootLoggingRes, "prefetch");
        final Resource logRes = resolver.create(rootLoggingRes, logResName, Collections.singletonMap("date", now));

        return new AemLogAppender(logRes);
    }
}
