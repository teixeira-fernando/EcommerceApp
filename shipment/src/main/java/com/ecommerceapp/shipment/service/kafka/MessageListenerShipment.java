package com.ecommerceapp.shipment.service.kafka;

import com.ecommerceapp.shipment.service.ShipmentService;
import com.ecommerceapp.shop.model.Order;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListenerShipment {

  private static final Logger logger = LogManager.getLogger(MessageListenerShipment.class);

  private CountDownLatch orderLatch = new CountDownLatch(1);

  @Autowired private ShipmentService service;

  @KafkaListener(
      topics = "${order.topic.name}",
      groupId = "${kafka.groupId}",
      containerFactory = "orderKafkaListenerContainerFactory")
  public void orderListener(Order order) {
    logger.info("Received Order: " + order);
    service.createOrderShipment(order);
    logger.info("Created a new shipment for the order {}", order);
    this.orderLatch.countDown();
  }
}
