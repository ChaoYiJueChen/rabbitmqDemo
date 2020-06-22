package com.everjiankang.dependency.demo11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
 
 public class Consumer1 {
	 
     private static final String EXCHANGE_NAME = "test_dlx_exchange";
     private  static final String QUEUE_NAME = "test_dlx_queue";
     private final static String ROUTING_KEY_NAME = "dlx.#";
 
     public static void main(String[] args) throws IOException {
         Connection connection = ConnectionUtil.getConnection();
         Channel channel = connection.createChannel();
         
         //1. 声明（创建）正常接收消息的队列
         Map<String,Object> arguments = new HashMap<>();
         arguments.put("x-dead-letter-exchange", "dlx.exchange");  //设置死信队列参数：交换机名称
         channel.exchangeDeclare(EXCHANGE_NAME,"topic");
         channel.queueDeclare(QUEUE_NAME,false,false,false,arguments); //本队列绑定死信队
         channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY_NAME);
         channel.basicQos(1); //允许一次多个消息进到队列里来
         
         //2. 声明（创建）死信队列
         channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
         channel.queueDeclare("dlx.queue", true, false, false,null);
         channel.queueBind("dlx.queue", "dlx.exchange", "#");
         
         Consumer consumer = new DefaultConsumer(channel) {
             @Override
             public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                 throws IOException {
                 super.handleDelivery(consumerTag, envelope, properties, body);
                 System.out.println(properties.getHeaders().get("id") + " | "+ new String(body,"UTF-8"));
                 boolean multiple = false;
                 if(properties.getHeaders().get("id").equals(1)) {
                	 try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                	boolean requeue = false; //重回队列
                	channel.basicNack(envelope.getDeliveryTag(), multiple, requeue);
                 } else {
                	 channel.basicAck(envelope.getDeliveryTag(), multiple); 
                 }
             }
         };
         boolean autoAck = false; //是否自动确认，设置为否
         channel.basicConsume(QUEUE_NAME,autoAck,consumer);
     }
 }