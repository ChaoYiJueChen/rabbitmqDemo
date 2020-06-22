package com.everjiankang.dependency.demo6;

import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 生产者端只发送消息给Exchange还有指定路由key,并不再指定具体队列名称了
 * 具体队列名称有消费者端创建，随便创建，只要其队列绑定到了本Exchange和路由即可
 */
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
	
     private static final String EXCHANGE_NAME = "test_exchange_topic";
     private final static String ROUTING_KEY_NAME = "order.update";
     
     public static void main(String[] args) throws IOException, TimeoutException {
         Connection connection = ConnectionUtil.getConnection();
         Channel channel = connection.createChannel();
         //声明交换机
         channel.exchangeDeclare(EXCHANGE_NAME,"topic");
         String message = "匹配insert";
         channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY_NAME,false,false,null,message.getBytes());
 
         channel.close();
         connection.close();
         System.out.println("game over");
     }
 }