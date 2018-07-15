package com.alexanderberndt.appintegration.core.impl;


import com.alexanderberndt.appintegration.api.AbstractIntegrationValidator;
import com.alexanderberndt.appintegration.api.IntegrationContext;
import com.alexanderberndt.appintegration.api.IntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexValidator extends AbstractIntegrationValidator<String> {

    public static final String TASK_NAME = "regex";

    public static final String REGEX_PARAM = "regex";

    final static private Logger LOGGER = LoggerFactory.getLogger(RegexValidator.class);

    private Pattern pattern;

    public RegexValidator() {
        super(String.class, REGEX_PARAM);
    }

    @Override
    public void postSetup() {
        final String regexString = this.getProperty(REGEX_PARAM, String.class);
        try {
            this.pattern = Pattern.compile(regexString);
        } catch (PatternSyntaxException e) {
            throw new IntegrationException("Non-parsable regex " + regexString, e);
        }
    }

    @Override
    protected void validate(String data, IntegrationContext context) {
        final Matcher m = pattern.matcher(data);
        if (m.find()) {
            LOGGER.info("Found pattern");
        }
    }
}
