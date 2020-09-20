package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.aem.engine.logging.AemLogAppender;
import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
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
import java.util.function.Consumer;
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


    @Override
    protected <R> R callRuntimeMethodWithContext(@Nonnull String applicationId, @Nonnull Function<AemGlobalContext, R> function) {
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(SUBSERVICE, SUB_SERVICE_ID))) {
            final LogAppender logAppender = new Slf4jLogAppender();
            final AemGlobalContext context = new AemGlobalContext(applicationId, factory, logAppender, resolver);

            final R result = function.apply(context);
            resolver.commit();

            return result;

        } catch (LoginException | PersistenceException e) {
            throw new AppIntegrationException("Cannot login to service user session!", e);
        }
    }

    @Override
    protected void callBackgroundMethodWithContext(@Nonnull String applicationId, @Nonnull Consumer<AemGlobalContext> consumer) {
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(SUBSERVICE, SUB_SERVICE_ID))) {
            final LogAppender logAppender = createLogAppender(resolver, applicationId);
            final AemGlobalContext context = new AemGlobalContext(applicationId, factory, logAppender, resolver);

            consumer.accept(context);
            resolver.commit();

        } catch (LoginException | PersistenceException e) {
            throw new AppIntegrationException("Cannot login to service user session!", e);
        }
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
