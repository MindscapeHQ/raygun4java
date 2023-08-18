package com.raygun.samplejakartaeeapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {
    private static final Properties properties = new Properties();

    static {
        InputStream in = ApplicationProperties.class.getResourceAsStream("/config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("You must create a config.properties file in src/main/resources that contains raygun.apiKey=<YOUR-RAYGUN-API-KEY>" + e);
        }
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }
}
