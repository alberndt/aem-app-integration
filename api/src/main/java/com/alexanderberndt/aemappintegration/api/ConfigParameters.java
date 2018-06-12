package com.alexanderberndt.aemappintegration.api;

import java.util.List;

public interface ConfigParameters {

    List<InputParameter> getInputParametersList();

    interface InputParameter {

        String getName();

        String getType();

    }

}
