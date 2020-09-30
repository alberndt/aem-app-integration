package com.alexanderberndt.appintegration.aem.engine.context;

import com.alexanderberndt.appintegration.aem.engine.AemContextProvider;
import com.alexanderberndt.appintegration.aem.engine.models.SlingApplicationInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PropertiesContextProvider implements AemContextProvider {

    /**
     * Calculates a key-value map, with variables and instance-specific values.
     *
     * @param instance Application instance
     * @return context map
     */
    @Nullable
    @Override
    public Map<String, String> getContext(SlingApplicationInstance instance) {

        final ValueMap valueMap = instance.getResource().getValueMap();
        return valueMap.keySet().stream()
                .filter(key -> !StringUtils.startsWithAny(key, "jcr:", "sling:"))
                .map(key -> new AbstractMap.SimpleImmutableEntry<>(key, valueMap.get(key, String.class)))
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
