package com.alexanderberndt.appintegration.api;

public abstract class AbstractIntegrationValidator<T> extends AbstractIntegrationTask<T, T> {

    public AbstractIntegrationValidator(Class<T> inputClass, String... requiredProperties) {
        super(inputClass, inputClass, requiredProperties);
    }

    @Override
    public final T filter(T data, IntegrationContext context) {
        this.validate(data, context);
        return data;
    }

    protected abstract void validate(T data, IntegrationContext context);
}
