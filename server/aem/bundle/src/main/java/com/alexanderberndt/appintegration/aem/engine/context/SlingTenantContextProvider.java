package com.alexanderberndt.appintegration.aem.engine.context;

import com.alexanderberndt.appintegration.aem.engine.AemContextProvider;
import com.alexanderberndt.appintegration.aem.engine.SlingApplicationInstance;
import org.apache.sling.tenant.Tenant;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class SlingTenantContextProvider implements AemContextProvider {

    /**
     * Calculates a key-value map, with variables and instance-specific values.
     *
     * @param instance Application instance
     * @return context map
     */
    @Nullable
    @Override
    public Map<String, String> getContext(SlingApplicationInstance instance) {
        final Tenant tenant = instance.getResource().adaptTo(Tenant.class);
        if (tenant == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> contextMap = new HashMap<>();
        final Iterator<String> propNameIter = tenant.getPropertyNames();
        while (propNameIter.hasNext()) {
            final String propName = propNameIter.next();
            final Object propValue = tenant.getProperty(propName);
            if (propValue instanceof String) {
                contextMap.put("tenant." + propName, (String) propValue);
            }
        }
        return contextMap;
    }

}
