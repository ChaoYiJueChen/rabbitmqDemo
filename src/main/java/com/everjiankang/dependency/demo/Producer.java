package com.everjiankang.dependency.demo;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;

public class Producer {

	public static void main(String[] args) throws Exception {
		//1. 创建一个ConnectionFactory，并进行配置
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setUsername("xiaochao");
		factory.setPassword("root");
		factory.setPort(5672);
		factory.setVirtualHost("/");
		
		//2. 通过连接工厂创建一个连接
		Connection connection = factory.newConnection();
		
		//3. 通过connection创建一个channel
		Channel channel = connection.createChannel();
		
		for (int i = 0; i < 5; i++) {
			//4. 通过channel发送数据
			String msg = "hello rabbitmq" + i;
			channel.basicPublish("", "test001", null, msg.getBytes());
		}
		
		//5. 记得要关闭相关的连接
		channel.close();
		connection.close();
		System.out.println("over");
	}

}
