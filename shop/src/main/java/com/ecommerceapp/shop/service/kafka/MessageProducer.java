package com.ecommerceapp.shop.service.kafka;

import com.ecommerceapp.domain.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

  private static final Logger logger = LogManager.getLogger(MessageProducer.class);

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired private KafkaTemplate<String, Order> orderKafkaTemplate;

  @Value(value = "${message.topic.name}")
  private String topicName;

  @Value(value = "${order.topic.name}")
  private String orderTopicname;

  public void sendString(String message) {
    logger.info("sending payload='{}' to topic='{}'", message, topicName);

    kafkaTemplate
        .send(topicName, message)
        .whenComplete(
            (result, ex) -> {
              if (ex == null) {
                logger.info(
                    "Sent message=[{}] with offset=[{}]",
                    message,
                    result.getRecordMetadata().offset());
              } else {
                logger.info("Unable to send message=[{}] due to: {}", message, ex.getMessage());
              }
            });
  }

  public void sendOrderToShipment(Order order) {
    logger.info("sending payload='{}' to topic='{}'", order, orderTopicname);

    orderKafkaTemplate.send(orderTopicname, order);
  }
}
