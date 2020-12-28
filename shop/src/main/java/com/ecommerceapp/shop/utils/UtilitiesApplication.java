package com.ecommerceapp.shop.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UtilitiesApplication {

  private static final Logger logger = LogManager.getLogger(UtilitiesApplication.class);

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (RuntimeException | JsonProcessingException e) {
      logger.error(e.getStackTrace());
    }
    return "";
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
      logger.error(ex.getStackTrace());
    }
    return prop.getProperty(propertyPath);
  }
}
