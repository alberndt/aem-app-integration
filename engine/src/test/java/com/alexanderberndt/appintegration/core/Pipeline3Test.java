package com.alexanderberndt.appintegration.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

public class Pipeline3Test {

    public static void main(String[] args) throws IOException {
        InputStream in = ClassLoader.getSystemResourceAsStream("com\\alexanderberndt\\appintegration\\core\\test-pipeline3.yaml");

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode node = mapper.readTree(in);

        System.out.println(node);

    }
}
