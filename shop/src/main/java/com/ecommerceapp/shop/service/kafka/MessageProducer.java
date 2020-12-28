package com.ecommerceapp.shop.service.kafka;

import com.ecommerceapp.shop.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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

    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

    future.addCallback(
        new ListenableFutureCallback<SendResult<String, String>>() {

          @Override
          public void onSuccess(SendResult<String, String> result) {
              logger.info(
                "Sent message=[",
                    message,
                    "] with offset=[",
                     result.getRecordMetadata().offset(),
                     "]");
          }

          @Override
          public void onFailure(Throwable ex) {
              logger.info(
                "Unable to send message=[" , message , "] due to : " , ex.getMessage());
          }
        });
  }

  public void sendOrderToShipment(Order order) {
    logger.info("sending payload='{}' to topic='{}'", order, orderTopicname);

    orderKafkaTemplate.send(orderTopicname, order);
  }
}
