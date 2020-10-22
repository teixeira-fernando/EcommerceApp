package com.ecommerceapp.shop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utilities {

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
