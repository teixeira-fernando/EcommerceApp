package com.ecommerceapp.inventory.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utilities {

  private static final Logger logger = LogManager.getLogger(Utilities.class);

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (RuntimeException | JsonProcessingException e) {
      logger.error(e.getStackTrace());
      throw new RuntimeException();
    }
  }
}
