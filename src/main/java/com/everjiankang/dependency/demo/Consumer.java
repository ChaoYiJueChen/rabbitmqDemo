package com.everjiankang.dependency.demo;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;

import java.util.Map;

import com.rabbitmq.client.Channel;

public class Consumer {

	public static void main(String[] args) throws Exception {
		//1. 创建一个ConnectionFactory，并进行配置
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setPort(5672);
		factory.setVirtualHost("/");
		
		//2. 通过连接工厂创建一个连接
		Connection connection = factory.newConnection();
		
		//3. 通过connection创建一个channel
		Channel channel = connection.createChannel();
		
		String queueName = "test001";
		//queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
		/**
		 * queue:     队列名称
		 * durable：	  是否持久化：即便是mq关闭了，再启动还是有的
		 * exclusive: 是否独占（只有我这一个channel,一个连接能监听，其他人都不可以，目的是保障顺序的消费。
		 * 			  或者所有的消息是多生产者，发出的消息有序的，但集群模式是由负载均衡的，a\b\c 3个节点分别消费3条，
		 * 			  但是你不能保障a\b\c他们消费的每一条数据的进度是有顺序的）：true
		 * autoDelete:队列如果与其他相关的Exchange没有绑定关系，脱离了绑定关系，那就自动删除
		 * arguments: 拓展参数
		 */
		//4. 声明(创建)一个队列
		channel.queueDeclare(queueName, true, false, false, null);
		
		com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel);
		
		
		//autoAck:是否自动签收，如果Broker和消费端进行监听数据，接收数据消费的过程中，
		//假设Broker有一条消息发送到了consumer端，这时候consumer端马上回送消息给Broker
		//告诉Broker，我接收到了消息。autoAck:true：自动签收;false:手动签收，需要在代码上指定
		channel.basicConsume(queueName, true,consumer);
		
//		Delivery delivery = consumer.
		
		//5. 记得要关闭相关的连接
		channel.close();
		connection.close();
		
	}

}
