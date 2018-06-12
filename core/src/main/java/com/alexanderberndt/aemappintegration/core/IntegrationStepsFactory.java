package com.alexanderberndt.aemappintegration.core;

import com.alexanderberndt.aemappintegration.api.HtmlValidator;

import java.util.List;

public interface IntegrationStepsFactory {

    List<String> listHtmlValidators();

    HtmlValidator getHtmlValidator(String pid);


}
