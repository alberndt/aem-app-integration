package com.alexanderberndt.appintegration.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public final class IntegrationUtil {


    private IntegrationUtil() {
    }

    public static JsonNode getJsonFromSystemResourceStream(String resourceName) throws IOException {
        return getJsonFromInputStream(ClassLoader.getSystemResourceAsStream(resourceName));
    }
    public static JsonNode getJsonFromResourceStream(String resourceName, Class resourceLoadingClass) throws IOException {
        return getJsonFromInputStream(resourceLoadingClass.getResourceAsStream(resourceName));
    }

    public static JsonNode getJson(String json) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(json);
    }


    public static <T> T parseJsonFromResourceStream(String resourceName, Class<T> clasz, Class resourceLoadingClass) throws IOException {
        return parseJsonFromInputStream(resourceLoadingClass.getClassLoader().getResourceAsStream(resourceName), clasz);
    }

    public static <T> T parseJsonFromSystemResourceStream(String resourceName, Class<T> clasz) throws IOException {
        return parseJsonFromInputStream(ClassLoader.getSystemResourceAsStream(resourceName), clasz);
    }

    public static JsonNode getJsonFromInputStream(InputStream inputStream) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Reader reader = new InputStreamReader(inputStream);
        return objectMapper.readTree(reader);
    }


    public static <T> T parseJsonFromInputStream(InputStream inputStream, Class<T> clasz) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        final Reader reader = new InputStreamReader(inputStream);
        return objectMapper.convertValue(objectMapper.readTree(reader), clasz);
    }

    public static ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }


}
