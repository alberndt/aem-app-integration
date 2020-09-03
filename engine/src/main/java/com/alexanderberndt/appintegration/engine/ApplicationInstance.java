package com.alexanderberndt.appintegration.engine;

import javax.annotation.Nonnull;

public interface ApplicationInstance {

    @Nonnull
    String getApplicationId();

    @Nonnull
    String getComponentId();

}
