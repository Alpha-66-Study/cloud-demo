package com.demo.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitmqApplicationTests {

  public static final String EXCHANGE_DIRECT = "exchange.direct.order";
  public static final String ROUTING_KEY = "order";

  public static final String EXCHANGE_DIRECT_TIMEOUT = "exchange.direct.timeout";
  public static final String ROUTING_KEY_TIMEOUT = "routing.key.timeout";


  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Test
  public void test01SendMessage() {
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, "Message Test Confirm");
  }

  @Test
  public void test02SendMessage() {
    for (int i = 0; i < 100; i++) {
      rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, "Test Prefetch " + i);
    }
  }

  @Test
  public void test03SendMessage() {
    for (int i = 0; i < 100; i++) {
      rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_TIMEOUT, ROUTING_KEY_TIMEOUT, "Test timeout " + i);
    }
  }

  @Test
  public void test04SendMessage() {
    // 创建消息后置处理器对象
    MessagePostProcessor postProcessor = message -> {
      // 设置消息的过期时间，单位是毫秒
      message.getMessageProperties().setExpiration("7000");
      return message;
    };
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_TIMEOUT, ROUTING_KEY_TIMEOUT, "Test timeout", postProcessor);
  }

}
