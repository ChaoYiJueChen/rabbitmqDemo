package com.everjiankang.dependency.demo7;

import java.io.IOException;
import java.util.Map;

import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
 
 public class Consumer2 {
	 
     private static final String EXCHANGE_NAME = "test_exchange_topic";
     private  static final String QUEUE_NAME = "test_queue_topic_2";
     private final static String ROUTING_KEY_NAME = "order.insert";
     
     public static void main(String[] args) throws IOException {
         Connection connection = ConnectionUtil.getConnection();
         Channel channel = connection.createChannel();
         channel.queueDeclare(QUEUE_NAME,false,false,false,null);
         channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY_NAME);
         channel.basicQos(1);
         Consumer consumer = new DefaultConsumer(channel) {
             @Override
             public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
                 super.handleDelivery(consumerTag, envelope, properties, body);
                 System.out.println("消息的header是：" + properties.getHeaders());
                 System.out.println("消息的过期时间是：" + properties.getExpiration());
                 
                 System.out.println("接收消息：" + new String(body, "UTF-8"));
             }
         };
         channel.basicConsume(QUEUE_NAME,true,consumer);
     }
 }