package com.ecommerceapp.shipment.service.kafka;

import com.ecommerceapp.shop.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class MessageListener {

  private static final Logger logger = LogManager.getLogger(MessageListener.class);

  private CountDownLatch orderLatch = new CountDownLatch(1);

  @KafkaListener(
      topics = "teste",
      groupId = "${kafka.groupId}",
      containerFactory = "stringKafkaListenerContainerFactory")
  public void messageListener(String message) {
    logger.info("Received message: " + message);
    this.orderLatch.countDown();
  }

  @KafkaListener(
      topics = "shipment",
      groupId = "${kafka.groupId}",
      containerFactory = "orderKafkaListenerContainerFactory")
  public void orderListener2(Order order) {
    logger.info("Received Order: " + order);
    this.orderLatch.countDown();
  }
}
