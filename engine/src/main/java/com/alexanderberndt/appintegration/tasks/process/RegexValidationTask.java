package com.alexanderberndt.appintegration.tasks.process;


import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
public class RegexValidationTask implements ProcessingTask {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String REGEX_PARAM = "regex";

    @Override
    public void process(TaskContext context, ExternalResource resource) {
        final String regexString = context.getValue(REGEX_PARAM, String.class);
        final Pattern pattern;
        try {
            pattern = Pattern.compile(regexString);
        } catch (PatternSyntaxException e) {
            throw new AppIntegrationException("Non-parsable regex " + regexString, e);
        }

        try {
            LOG.info("validate regex {}", pattern);
//        if (resource.isText()) {
            final Matcher m;
            // ToDo: Re-implement based on reader
            m = pattern.matcher(resource.getContentAsParsedObject(String.class));
            if (m.find()) {
                LOG.info("Found pattern");
                context.addWarning(String.format("Found pattern %s", pattern.pattern()));
            }
//        } else {
//            LOG.warn("Resource {} is not text data!", resource);
//            context.addWarning("Resource {} is not text data!", resource);
//        }
        } catch (IOException e) {
            context.addError(e.getMessage());
        }
    }

    @Override
    public void declareTaskPropertiesAndDefaults(TaskContext taskContext) {
        taskContext.setType(REGEX_PARAM, String.class);
    }
}
