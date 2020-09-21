package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;

import javax.annotation.Nonnull;
import java.util.List;

public interface AppIntegrationEngine<I extends ApplicationInstance> {

    ExternalResource getHtmlSnippet(@Nonnull I instance);

    ExternalResource getStaticResource(@Nonnull String applicationId, @Nonnull String relativePath);

    boolean isDynamicPath(@Nonnull String applicationId, String relativePath);

    List<String> getDynamicPaths(@Nonnull String applicationId);

    void prefetch(@Nonnull List<I> applicationInstanceList);
}
