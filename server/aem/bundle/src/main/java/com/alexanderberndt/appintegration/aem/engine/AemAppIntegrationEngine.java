package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.aem.engine.logging.AemLogAppender;
import com.alexanderberndt.appintegration.engine.AbstractAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

@Component(service = AemAppIntegrationEngine.class)
public class AemAppIntegrationEngine extends AbstractAppIntegrationEngine<SlingApplicationInstance, AemGlobalContext> implements AppIntegrationEngine<SlingApplicationInstance> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String SUB_SERVICE_ID = "engine";

    @Reference
    private AemAppIntegrationFactory factory;

    @Reference
    private ResourceResolverFactory resolverFactory;


    @Override
    public ExternalResource getHtmlSnippet(@Nonnull SlingApplicationInstance instance) {
        return callRuntimeMethodWithContext(instance.getApplicationId(), context -> super.getHtmlSnippet(context, instance));
    }

    @Override
    public ExternalResource getStaticResource(@Nonnull String applicationId, @Nonnull String relativePath) {
        return callRuntimeMethodWithContext(applicationId, context -> super.getStaticResource(context, relativePath));
    }

    @Override
    public boolean isDynamicPath(@Nonnull String applicationId, String relativePath) {
        return callRuntimeMethodWithContext(applicationId, context -> super.isDynamicPath(context, relativePath));
    }

    @Override
    public List<String> getDynamicPaths(@Nonnull String applicationId) {
        return callRuntimeMethodWithContext(applicationId, super::getDynamicPaths);
    }

    @Override
    public void prefetch(@Nonnull List<SlingApplicationInstance> applicationInstanceList) {
        groupInstancesByApplicationId(applicationInstanceList,
                (applicationId, groupedInstanceList) ->
                        callBackgroundMethodWithContext(applicationId, context -> super.prefetch(context, groupedInstanceList))
        );
    }

    private <R> R callRuntimeMethodWithContext(@Nonnull String applicationId, @Nonnull Function<AemGlobalContext, R> function) {
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(SUBSERVICE, SUB_SERVICE_ID))) {
            final LogAppender logAppender = new Slf4jLogAppender();
            final AemExternalResourceCache cache = new AemExternalResourceCache(resolver, applicationId);
            final AemGlobalContext context = new AemGlobalContext(applicationId, factory, cache, logAppender, resolver);

            final R result = function.apply(context);
            resolver.commit();

            return result;

        } catch (LoginException | PersistenceException e) {
            throw new AppIntegrationException("Cannot login to service user session!", e);
        }
    }

    private void callBackgroundMethodWithContext(@Nonnull String applicationId, @Nonnull Consumer<AemGlobalContext> consumer) {
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(SUBSERVICE, SUB_SERVICE_ID))) {
            final LogAppender logAppender = createPersistentLogAppender(resolver, applicationId);
            final AemExternalResourceCache cache = new AemExternalResourceCache(resolver, applicationId);
            final AemGlobalContext context = new AemGlobalContext(applicationId, factory, cache, logAppender, resolver);

            consumer.accept(context);
            resolver.commit();

        } catch (LoginException | PersistenceException e) {
            throw new AppIntegrationException("Cannot login to service user session!", e);
        }
    }

    public LogAppender createPersistentLogAppender(@Nonnull ResourceResolver resolver, @Nonnull String applicationId) throws PersistenceException {

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
