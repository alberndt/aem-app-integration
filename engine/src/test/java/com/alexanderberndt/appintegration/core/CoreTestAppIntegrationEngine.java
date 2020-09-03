package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class CoreTestAppIntegrationEngine extends AppIntegrationEngine<CoreTestAppInstance, CoreTestGlobalContext> {

    @Nonnull
    private final CoreAppIntegrationFactory factory;

    @Nonnull
    private final Supplier<LogAppender> appenderSupplier;

    public CoreTestAppIntegrationEngine(@Nonnull CoreAppIntegrationFactory factory, @Nonnull final Supplier<LogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Nonnull
    @Override
    protected AppIntegrationFactory<CoreTestAppInstance, CoreTestGlobalContext> getFactory() {
        return factory;
    }

    /**
     * Implementation of this method shall create a {@link GlobalContext} and call {@link #prefetch(CoreTestGlobalContext, String, List)}
     * to do the actual prefetch.
     *
     * @param applicationId           Application ID
     * @param applicationInstanceList List of application instances, with all instances linking the application id of the 1st parameter
     */
    @Override
    protected void createContextAndPrefetch(String applicationId, List<CoreTestAppInstance> applicationInstanceList) throws IOException {
        prefetch(new CoreTestGlobalContext(appenderSupplier.get()), applicationId, applicationInstanceList);
    }


}
