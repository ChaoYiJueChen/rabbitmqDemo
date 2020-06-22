package com.everjiankang.dependency.demo5;

import java.io.IOException;

import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
 
 public class Consumer1 {
     private final static String QUEUE_NAME = "test_queue_direct_A";
     private final static String EXCHANGE_NAME = "test_exchange_direct";
     private final static String ROUTING_KEY_NAME = "A";
     public static void main(String[] argv) throws Exception {
         Connection connection = ConnectionUtil.getConnection();
         Channel channel = connection.createChannel();
        
         // 声明队列
         channel.queueDeclare(QUEUE_NAME, false, false, false, null);
         channel.exchangeDeclare(EXCHANGE_NAME,"direct"); //直连
         
         //绑定队列到交换机
         channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_NAME);
 
         // 同一时刻服务器只会发一条消息给消费者
         channel.basicQos(1);
 
         // 定义队列的消费者
         Consumer consumer = new DefaultConsumer(channel) {
             @Override
             public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                 throws IOException {
                 super.handleDelivery(consumerTag, envelope, properties, body);
                 System.out.println(new String(body,"UTF-8"));
             }
         };
         channel.basicConsume(QUEUE_NAME,true,consumer);
     }
 }