package com.alexanderberndt.aemappintegration.api;

public interface Validator<T, C> extends IntegrationStep<C, T, T> {

    void validate(T data, C config, IntegrationContext context);

}
