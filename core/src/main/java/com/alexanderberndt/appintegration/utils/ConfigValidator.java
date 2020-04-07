package com.alexanderberndt.appintegration.utils;

import com.alexanderberndt.appintegration.engine.pipeline.api.IntegrationStepResult;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class ConfigValidator {

//    private final ImmutableConfiguration configuration;
//    private final IntegrationStepResult<?> result = new IntegrationStepResult<>();
//
//    public ConfigValidator(ImmutableConfiguration configuration) {
//        this.configuration = configuration;
//        this.result.setStatus(IntegrationStepResult.Status.OK);
//    }
//
//    public <T> ConfigParamValidator<T> validateParam(String param, Class<T> tClass) {
//        return new ConfigParamValidator<>(param, tClass);
//    }
//
//    public IntegrationStepResult<?> getResult() {
//        return result;
//    }
//
//    public class ConfigParamValidator<T> {
//
//        private final String param;
//        private final Class<T> tClass;
//
//        private boolean isValid = true;
//
//        private ConfigParamValidator(String param, Class<T> tClass) {
//            this.param = param;
//            this.tClass = tClass;
//
//            this.validateType();
//        }
//
//        public ConfigParamValidator<T> required() {
//
//            if (isValid && !configuration.containsKey(param)) {
//                isValid = false;
//                result.addError("Missing config-parameter '{}'", param);
//            }
//
//            return this;
//        }
//
//        private ConfigParamValidator<T> validateType() {
//
//            if (isValid && configuration.containsKey(param)) {
//                try {
//                    configuration.get(tClass, param);
//                } catch (ConversionException e) {
//                    isValid = false;
//                    result.addError("Cannot convert param {} to type {}, due to {}", param, tClass.getSimpleName(), e.getMessage());
//                }
//            }
//
//            return this;
//        }
//
//
//        public ConfigParamValidator<T> nonBlank() {
//
//            if (isValid && configuration.containsKey(param) && StringUtils.isBlank(configuration.getString(param))) {
//                isValid = false;
//                result.addError("config-parameter '{}' MUST NOT be blank", param);
//            }
//
//            return this;
//        }
//
//
//        public ConfigParamValidator<T> validate(Function<T, String> validator) {
//
//            if (isValid && configuration.containsKey(param)) {
//
//                final String errorMsg = validator.apply(configuration.get(tClass, param));
//                if (StringUtils.isNotBlank(errorMsg)) {
//                    isValid = false;
//                    result.addError(errorMsg);
//                }
//
//            }
//
//            return this;
//        }
//    }
}
