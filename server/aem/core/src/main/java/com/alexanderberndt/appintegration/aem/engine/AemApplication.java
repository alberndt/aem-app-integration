package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.Application;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component(service = AemApplication.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = AemApplication.Configuration.class, factory = true)
public class AemApplication extends Application {

    @ObjectClassDefinition(name = "AEM App-Integration - Application")
    @interface Configuration {

        @AttributeDefinition(
                name = "Application Id",
                description = "Unique identifier for the application."
        )
        @Nonnull
        String applicationId();


        @AttributeDefinition(
                name = "Application-Info URL",
                description = "URL to the application-info.json file. This will be the base path for the entire application."
        )
        @Nonnull
        String applicationInfoUrl();


        @AttributeDefinition(
                name = "Resource-Loader Name",
                description = "Name of the used ResourceLoader (e.g. http)."
        )
        @Nonnull
        String resourceLoaderName() default "http";

        @AttributeDefinition(
                name = "Processing-Pipeline Name",
                description = "Name of the used processing-pipeline (e.g. default)."
        )
        @Nonnull
        String processingPipelineName() default "default";

        @AttributeDefinition(
                name = "Context-Provider Names",
                description = "List of context providers, that shall be used to determine the context of the instances."
        )
        @Nullable
        String[] contextProviderNames() default {"locale", "tenant"};

        @AttributeDefinition(
                name = "Global Properties",
                description = "List of key=value with global processing properties, which will overwrite the task-properties."
        )
        @Nullable
        String[] globalProperties() default {};
    }

    private final String applicationId;

    @Activate
    public AemApplication(@Nonnull Configuration configuration) {
        super(configuration.applicationInfoUrl(), configuration.resourceLoaderName(), configuration.processingPipelineName(),
                Arrays.asList(configuration.contextProviderNames()), convertToMap(configuration.globalProperties()));
        this.applicationId = configuration.applicationId();
    }

    public String getApplicationId() {
        return applicationId;
    }

    @Override
    public String toString() {
        return "AemApplication{" +
                "applicationId='" + applicationId + '\'' +
                ", applicationInfoUrl='" + getApplicationInfoUrl() + '\'' +
                ", resourceLoaderName='" + getResourceLoaderName() + '\'' +
                ", contextProviderNames=" + getContextProviderNames() +
                ", processingPipelineName='" + getProcessingPipelineName() + '\'' +
                ", globalProperties=" + getGlobalProperties() +
                '}';
    }

    private static Map<String, Object> convertToMap(String[] keyValueStrings) {
        final Map<String, Object> map = new HashMap<>();
        for (String keyValueString : keyValueStrings) {
            final String[] splitKeyValue = StringUtils.split(keyValueString, "=", 2);
            if ((splitKeyValue != null) && (splitKeyValue.length == 2)) {
                map.put(splitKeyValue[0].trim(), splitKeyValue[1].trim());
            }
        }
        return map;
    }
}
