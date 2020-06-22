package com.everjiankang.dependency.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeQueueBindingConfig {
	
	/**
	 * 针对消费者配置
	 * 1.设置交换机类型
	 * 2.将队列绑定到交换机
	 * 		FanoutExchange:将消息分发到所有的绑定队列，无routingKey的概念
	 * 		HeaderExchange：通过添加属性key-value匹配
	 * 		DirectExchange：按照routingKey分发到指定队列
	 * 		TopicExchange：多关键字匹配
	 */
	//交换机1、队列1、绑定1
	@Bean
	public TopicExchange exchange01() {
		return new TopicExchange("topicExchange01",true,false);
	}
	@Bean
	public Queue queue01() {
		return new Queue("queue01", true);
	}
	@Bean
	public Binding bingding01() {
		return BindingBuilder.bind(queue01()).to(exchange01()).with("spring.*");
	}
	
	//交换机2、队列2、绑定2
	@Bean
	public TopicExchange exchange02() {
		return new TopicExchange("topicExchange02",true,false);
	}
	@Bean
	public Queue queue02() {
		return new Queue("queue02", true);
	}
	@Bean
	public Binding bingding02() {
		return BindingBuilder.bind(queue02()).to(exchange02()).with("rabbit.*");
	}
	
	//交换机1、队列3、绑定3
	@Bean
	public Queue queue03() {
		return new Queue("queue03", true);
	}
	@Bean
	public Binding bingding03() {
		return BindingBuilder.bind(queue03()).to(exchange01()).with("mq.*");
	}
	
	//其它队列设置
	@Bean
	public Queue queue_image() {
		return new Queue("queue_image", true);
	}
	@Bean
	public Queue queue_pdf() {
		return QueueBuilder.durable("queue_pdf").build();
	}
}