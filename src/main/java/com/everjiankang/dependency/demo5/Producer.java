package com.everjiankang.dependency.demo5;

import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 生产者端只发送消息给Exchange还有指定路由key,并不再指定具体队列名称了
 * 具体队列名称有消费者端创建，随便创建，只要其队列绑定到了本Exchange和路由即可
 */
public class Producer {

    private static final String EXCHANGE_NAME = "test_exchange_direct";

    public static void main(String[] argv) throws Exception {
         // 获取到连接以及mq通道
         Connection connection = ConnectionUtil.getConnection();
         Channel channel = connection.createChannel();
 
         // 声明exchange,路由模式声明direct
         channel.exchangeDeclare(EXCHANGE_NAME, "direct");
 
         // 消息内容
         String messageB = "这是消息B";
         String routingKeyB = "B";
         channel.basicPublish(EXCHANGE_NAME, routingKeyB, null, messageB.getBytes());
         System.out.println(" [生产者] Sent '" + messageB + "'");
         
         String messageA = "这是消息A";
         String routingKeyA = "A";
         channel.basicPublish(EXCHANGE_NAME, routingKeyA, null, messageA.getBytes());
         System.out.println(" [生产者] Sent '" + messageA + "'");
 
         channel.close();
         connection.close();
         System.out.println("game over");
     }
 
 }