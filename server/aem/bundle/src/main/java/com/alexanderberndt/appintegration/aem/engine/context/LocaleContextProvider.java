package com.alexanderberndt.appintegration.aem.engine.context;

import com.alexanderberndt.appintegration.aem.engine.AemContextProvider;
import com.alexanderberndt.appintegration.aem.engine.models.SlingApplicationInstance;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class LocaleContextProvider implements AemContextProvider {

    /**
     * Calculates a key-value map, with variables and instance-specific values.
     *
     * @param instance Application instance
     * @return context map
     */
    @Nullable
    @Override
    public Map<String, String> getContext(SlingApplicationInstance instance) {

        final Resource instanceResource = instance.getResource();

        return Optional.of(instanceResource)
                .map(Resource::getResourceResolver)
                .map(resolver -> resolver.adaptTo(PageManager.class))
                .map(pageManager -> pageManager.getContainingPage(instanceResource))
                .map(Page::getLanguage)
                .map(locale -> {
                    final Map<String, String> contextMap = new HashMap<>();
                    contextMap.put("language", locale.getLanguage());
                    contextMap.put("country", locale.getCountry());
                    contextMap.put("locale", locale.toLanguageTag());
                    return contextMap;
                })
                .orElseGet(Collections::emptyMap);
    }
}
