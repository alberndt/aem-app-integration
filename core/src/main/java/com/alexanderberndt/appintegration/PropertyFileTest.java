
package com.alexanderberndt.appintegration;

import java.io.IOException;
import java.util.Properties;

public class PropertyFileTest {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + ": '" + properties.getProperty(key) + "'");

        }
    }
}
