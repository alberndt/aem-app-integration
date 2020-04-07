package com.alexanderberndt.appintegration.parser;

import com.alexanderberndt.appintegration.api.AppIntegrationException;
import com.alexanderberndt.appintegration.engine.pipeline.api.IntegrationResourceType;
import com.alexanderberndt.appintegration.IntegrationJobImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class IntegrationJobParser {

//    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationJobParser.class);
//
//    private IntegrationJobParser() {
//    }
//
//    public static IntegrationJobImpl parseTaskPipelineYaml(String yaml, IntegrationTaskFactory factory) {
//        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//        try {
//            JsonNode node = objectMapper.readTree(yaml);
//            IntegrationJobDef jobDef = objectMapper.convertValue(node, IntegrationJobDef.class);
//            LOGGER.info("parsed job {}", jobDef);
//            return convertJobDefToJob(jobDef, factory);
//        } catch (IOException e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//
//        return null;
//    }
//
//    private static IntegrationJobImpl convertJobDefToJob(IntegrationJobDef jobDef, IntegrationTaskFactory factory) {
//        try {
//            IntegrationJobImpl job = new IntegrationJobImpl(jobDef.getApplicationId());
//            job.setBaseURI(jobDef.getBaseUrl());
//            job.setHtmlSnippetUrl(jobDef.getHtmlSnippetUrl());
//            job.setHtmlSnippetQuery(jobDef.getHtmlSnippetQuery());
//
//            for (IntegrationTaskDef taskDef : jobDef.getTasks()) {
//                IntegrationTask task = factory.createTask(taskDef.getTask());
//                try {
//                    List<IntegrationResourceType> expectedResourceTypes = taskDef.getResourceTypes().stream()
//                            .map(IntegrationResourceType::parse)
//                            .collect(Collectors.toList());
//                    task.setApplicableResourceTypes(expectedResourceTypes);
//                } catch (IllegalArgumentException e) {
//                    throw new AppIntegrationException("Cannot parse expected resource types " + taskDef.getResourceTypes(), e);
//                }
//                task.setupTask(taskDef.getProperties());
//                job.addIntegrationTask(task);
//            }
//            return job;
//        } catch (Exception e) {
//            throw new AppIntegrationException("Failed to create job", e);
//        }
//    }
}
