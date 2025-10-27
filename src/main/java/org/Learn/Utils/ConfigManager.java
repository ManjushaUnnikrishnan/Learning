package org.Learn.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();

        try {
            FileInputStream fis = new FileInputStream("src/main/resources/_config.properties");
            properties.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static synchronized ConfigManager getInstance() {
        if(instance == null)
            instance = new ConfigManager();
        return instance;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
