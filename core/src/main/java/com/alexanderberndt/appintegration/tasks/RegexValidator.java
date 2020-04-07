package com.alexanderberndt.appintegration.tasks;


import com.alexanderberndt.appintegration.engine.pipeline.api.AbstractPipelineFilter;
import com.alexanderberndt.appintegration.api.AppIntegrationException;
import com.alexanderberndt.appintegration.engine.pipeline.api.IntegrationJob;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexValidator /*extends AbstractPipelineFilter*/ {

//    public static final String TASK_NAME = "validate-regex";
//
//    public static final String REGEX_PARAM = "regex";
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(RegexValidator.class);
//
//    private Pattern pattern;
//
//    public RegexValidator() {
//        super(REGEX_PARAM);
//    }
//
//    @Override
//    public void postSetup() {
//        final String regexString = this.getProperty(REGEX_PARAM, String.class);
//        try {
//            this.pattern = Pattern.compile(regexString);
//        } catch (PatternSyntaxException e) {
//            throw new AppIntegrationException("Non-parsable regex " + regexString, e);
//        }
//    }
//
//    @Override
//    public void execute(ProcessingItem resource, IntegrationJob job) {
//        LOGGER.info("validate regex {}", pattern);
//        if (resource.isText()) {
//            final Matcher m = pattern.matcher(resource.getDataAsString());
//            if (m.find()) {
//                LOGGER.info("Found pattern");
//                job.addWarning("Found pattern {}", pattern.pattern());
//            }
//        } else {
//            LOGGER.warn("Resource {} is not text data!", resource);
//            job.addWarning("Resource {} is not text data!", resource);
//        }
//    }
}
