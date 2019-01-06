package com.alexanderberndt.appintegration.api;

import org.apache.commons.configuration2.ImmutableConfiguration;

public interface IntegrationStep {

    /**
     * Only validates a potential configuration. It shall NOT store the config, as it might me changed later.
     * Returns null or an empty OperationResult.
     * @param configuration configuration
     */
    IntegrationStepResult<?> validateConfiguration(ImmutableConfiguration configuration);

}
