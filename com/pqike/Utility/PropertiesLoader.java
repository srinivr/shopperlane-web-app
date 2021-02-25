/*
 * Author: Srinivas
 * Read values from a config file
 */
package com.pqike.Utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author User
 */
public class PropertiesLoader {

    private static final String PROPERTIESFILE = "shopperlane.properties";
    private static final Properties PROPERTIES = new Properties();
    private static PropertiesLoader instance = null;
    private PropertiesLoader(){
        
    }
    
    public static synchronized PropertiesLoader getInstance(){
        if(instance ==  null)
            instance = new PropertiesLoader();
        return instance;
    }
    static {

//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        InputStream propertiesFile = classLoader.getResourceAsStream(PROPERTIESFILE);
//
//        if (propertiesFile == null) {
//            throw new RuntimeException(
//                    "Properties file '" + PROPERTIESFILE + "' is missing in classpath.");
//        }

        try {
            PROPERTIES.load(new FileInputStream("shopperlane.properties"));
            System.out.println("Properties loaded");
        } catch (IOException e) {
            throw new RuntimeException(
                    "Cannot load properties file '" + PROPERTIESFILE + "'.", e);
        }

    }

    public String getProperty(String fullKey, boolean mandatory) throws RuntimeException {
        String property = PROPERTIES.getProperty(fullKey);
        if (property == null || property.trim().length() == 0) {
            if (mandatory) {
                throw new RuntimeException("Required property '" + fullKey + "'"
                        + " is missing in properties file '" + PROPERTIESFILE + "'.");
            } else {
                // Make empty value null. Empty Strings are evil.
                property = null;
            }
        }
        return property;
    }

}
