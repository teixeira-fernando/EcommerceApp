package com.ecommerceapp.shop.service.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  @Value(value = "${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Value(value = "${message.topic.name}")
  private String messageTopicName;

  @Value(value = "${order.topic.name}")
  private String orderTopicName;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic topicString() {
    System.out.println("created topic: " + messageTopicName);
    return new NewTopic(messageTopicName, 1, (short) 1);
  }

  @Bean
  public NewTopic topicOrder() {
    System.out.println("created topic: " + orderTopicName);
    return new NewTopic(orderTopicName, 1, (short) 1);
  }
}
