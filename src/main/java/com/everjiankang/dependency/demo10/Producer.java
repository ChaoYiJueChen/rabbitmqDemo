package com.everjiankang.dependency.demo10;

/**
 * 生产者端只发送消息给Exchange还有指定路由key,并不再指定具体队列名称了
 * 具体队列名称有消费者端创建，随便创建，只要其队列绑定到了本Exchange和路由即可
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Producer {
	
     private static final String EXCHANGE_NAME = "test_exchange_topic";
     private final static String ROUTING_KEY_NAME = "order.update";
     
     public static void main(String[] args) throws IOException, TimeoutException {
         Connection connection = ConnectionUtil.getConnection();
         Channel channel = connection.createChannel();
         //声明交换机
         channel.exchangeDeclare(EXCHANGE_NAME,"topic");
         //发送五条消息，属性参数properties
         for (int i = 0; i < 3; i++) {
        	//【设置属性参数】：AMQP.BasicProperties() start
        	 Map<String,Object> headers = new HashMap<>();
        	 headers.put("id", i);
        	 headers.put("name", "xiaochao");
        	 AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
        			 .contentEncoding("UTF-8")
        			 .deliveryMode(2)
        			 .expiration("10000")
        			 .headers(headers)
        			 .build();
        	 String message = "匹配insert" + i;
        	 channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY_NAME,true,properties,message.getBytes());
         }
         channel.close();
         connection.close();
         System.out.println("game over");
     }
 }