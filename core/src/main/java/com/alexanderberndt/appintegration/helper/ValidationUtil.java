package com.alexanderberndt.appintegration.helper;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ValidationUtil {

    private ValidationUtil() {
    }

    @Nonnull
    public static <I> I requireNotNull(@Nullable I obj, Supplier<String> errorMessageSupplier) {
        if (obj != null) {
            return obj;
        } else {
            throw new AppIntegrationException(errorMessageSupplier.get());
        }
    }
}
