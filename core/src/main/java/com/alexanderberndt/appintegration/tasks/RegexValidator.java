package com.alexanderberndt.appintegration.tasks;


import com.alexanderberndt.appintegration.api.task.ProcessingTask;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexValidator implements ProcessingTask {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String REGEX_PARAM = "regex";

    @Override
    public String getName() {
        return "regex-validator";
    }

    @Override
    public void process(ProcessingContext context, ExternalResource resource) {
        final String regexString = context.getParametersMap().require(REGEX_PARAM, String.class);
        final Pattern pattern;
        try {
            pattern = Pattern.compile(regexString);
        } catch (PatternSyntaxException e) {
            throw new AppIntegrationException("Non-parsable regex " + regexString, e);
        }

        LOG.info("validate regex {}", pattern);
        // ToDo: Implement
        throw new UnsupportedOperationException("Not yet implemented");
//        if (resource.isText()) {
//            final Matcher m = pattern.matcher(resource.getDataAsString());
//            if (m.find()) {
//                LOG.info("Found pattern");
//                context.addWarning("Found pattern {}", pattern.pattern());
//            }
//        } else {
//            LOG.warn("Resource {} is not text data!", resource);
//            context.addWarning("Resource {} is not text data!", resource);
//        }
    }

}
