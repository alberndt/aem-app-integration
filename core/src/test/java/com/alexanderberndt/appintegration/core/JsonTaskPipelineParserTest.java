package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.api.definition.IntegrationPipelineDef;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

import java.io.InputStream;

public class JsonTaskPipelineParserTest {

    @Test
    public void yamlTest() {

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            InputStream in = this.getClass().getResourceAsStream("test-pipeline1.yaml");
            System.out.println(in);

            IntegrationPipelineDef integrationPipelineDef = mapper.readValue(in, IntegrationPipelineDef.class);
            System.out.println(integrationPipelineDef);

            //JsonNode node = mapper.readTree(in);
            //new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, node);


//            Iterator<String> fieldNamesIter = node.fieldNames();
//            while (fieldNamesIter.hasNext()) {
//                final String fieldName = fieldNamesIter.next();
//                System.out.println("#### " + fieldName + "####");
//
//
//                final JsonNode fieldValue = node.get(fieldName);
//
//
//                if (fieldValue.isValueNode()) {
//                    System.out.println("value: " + fieldValue.isTextual() + " - " + fieldValue.toString());
//                } else if (fieldValue.isArray()) {
//
//                    System.out.println("array else " + fieldValue);
//
//                }
//
//
//                else {
//                    System.out.println("anything else " + fieldValue);
//                }
//
//                System.out.println("---------------------");
//            }




        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}