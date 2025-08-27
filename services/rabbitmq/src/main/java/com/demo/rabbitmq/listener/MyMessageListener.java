package com.demo.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MyMessageListener {

  public static final String EXCHANGE_DIRECT = "exchange.direct.order";
  public static final String ROUTING_KEY = "order";
  public static final String QUEUE_NAME = "queue.order";

  public static final String EXCHANGE_DIRECT_TIMEOUT = "exchange.direct.timeout";
  public static final String ROUTING_KEY_TIMEOUT = "routing.key.timeout";
  public static final String QUEUE_NAME_TIMEOUT = "queue.timeout";


  @RabbitListener(bindings = @QueueBinding(
      exchange = @Exchange(value = EXCHANGE_DIRECT),
      key = {ROUTING_KEY},
      value = @Queue(value = QUEUE_NAME, durable = "true")
  ))
  public void processMessage(String dataString, Message message, Channel channel) throws IOException {
    long deliveryTag = message.getMessageProperties().getDeliveryTag();

    try {
      // 核心操作
      log.info("消费端1接收到了消息：{}", dataString);

      System.out.println(10 / 0);

      // 核心操作成功：返回 ACK 信息
      channel.basicAck(deliveryTag, false);

    } catch (Exception e) {

      // 获取当前消息是否是重复投递的
      //      redelivered 为 true：说明当前消息已经重复投递过一次了
      //      redelivered 为 false：说明当前消息是第一次投递
      Boolean redelivered = message.getMessageProperties().getRedelivered();

      // 核心操作失败：返回 NACK 信息
      // requeue 参数：控制消息是否重新放回队列
      //      取值为 true：重新放回队列，broker 会重新投递这个消息
      //      取值为 false：不重新放回队列，broker 会丢弃这个消息

      if (redelivered) {
        // 如果当前消息已经是重复投递的，说明此前已经重试过一次啦，所以 requeue 设置为 false，表示不重新放回队列
        channel.basicNack(deliveryTag, false, false);
      } else {
        // 如果当前消息是第一次投递，说明当前代码是第一次抛异常，尚未重试，所以 requeue 设置为 true，表示重新放回队列在投递一次
        channel.basicNack(deliveryTag, false, true);
      }

      // reject 表示拒绝
      // 辨析：basicNack() 和 basicReject() 方法区别
      // basicNack()能控制是否批量操作
      // basicReject()不能控制是否批量操作
      // channel.basicReject(deliveryTag, true);
    }

  }

  @RabbitListener(bindings = @QueueBinding(
      exchange = @Exchange(value = EXCHANGE_DIRECT_TIMEOUT),
      key = {ROUTING_KEY_TIMEOUT},
      value = @Queue(value = QUEUE_NAME_TIMEOUT, durable = "true")
  ))
  public void processMessage2(String dataString, Message message, Channel channel) {
    log.info("消费端2接收到了消息：{}", dataString);
  }

}
