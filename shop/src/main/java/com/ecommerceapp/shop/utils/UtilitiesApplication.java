package com.ecommerceapp.shop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UtilitiesApplication {

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String readPropertyValue(String propertyPath) {
    Properties prop = new Properties();

    try (InputStream input =
        UtilitiesApplication.class.getClassLoader().getResourceAsStream("application.properties")) {

      if (input == null) {
        throw new FileNotFoundException(
            "unable to find application.properties to read the property value");
      }
      // load a properties file from class path, inside static method
      prop.load(input);

    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return prop.getProperty(propertyPath);
  }
}
