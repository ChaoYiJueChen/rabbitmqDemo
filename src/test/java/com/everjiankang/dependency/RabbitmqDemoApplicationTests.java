package com.everjiankang.dependency;


//import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.Binding.DestinationType;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Exchange;
//import org.springframework.amqp.core.ExchangeBuilder;
//import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.everjiankang.dependency.model.Dog;
import com.everjiankang.dependency.model.User;

@SpringBootTest
class RabbitmqDemoApplicationTests {
	
	@Autowired
	private RabbitAdmin rabbitAdmin;

//	@Test
//	void contextLoads() {
//		Exchange exchange1 = new ExchangeBuilder("test.direct.exchange", ExchangeTypes.DIRECT).build();
//		Exchange exchange2 = new ExchangeBuilder("test.topic.exchange", ExchangeTypes.TOPIC).build();
//		Exchange exchange3 = new ExchangeBuilder("test.fanout.exchange", ExchangeTypes.FANOUT).build();
//		
//		Queue queue1 = QueueBuilder.durable("test.direct.queue").autoDelete().expires(1500000).build();
//		Queue queue2 = QueueBuilder.durable("test.topic.queue").autoDelete().expires(1500000).build();
//		Queue queue3 = QueueBuilder.durable("test.fanout.queue").autoDelete().expires(1500000).build();
//		
//		Binding binding1 = new Binding("test.direct.queue", DestinationType.QUEUE, "test.direct.exchange", "directRoutingKey", new HashMap<>());
//		Binding binding2 = BindingBuilder.bind(queue2).to(exchange2).with("topicRoutingKey.#").and(new HashMap<>());
//		Binding binding3 = BindingBuilder.bind(queue3).to(exchange3).with("").noargs();//fanout不需要routingKey
//		
//		rabbitAdmin.declareExchange(exchange1);
//		rabbitAdmin.declareExchange(exchange2);
//		rabbitAdmin.declareExchange(exchange3);
//		
//		rabbitAdmin.declareQueue(queue1);
//		rabbitAdmin.declareQueue(queue2);
//		rabbitAdmin.declareQueue(queue3);
//		
//		rabbitAdmin.declareBinding(binding1);
//		rabbitAdmin.declareBinding(binding2);
//		rabbitAdmin.declareBinding(binding3);
//		rabbitAdmin.purgeQueue("test.direct.queue", true);//清空队列
//		
//	}
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Test
	public void testSendMessage() {
		MessageProperties messageProperties = MessagePropertiesBuilder.newInstance().build();
		messageProperties.getHeaders().put("desc", "信息描述");
		messageProperties.getHeaders().put("type", "自定义消息类型");
		String bodyStr = "这是消息体";
		Message message = new Message(bodyStr.getBytes(), messageProperties);
		rabbitTemplate.convertAndSend("topicExchange01", "spring.amqp", message,new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				System.out.println("----添加额外的设置----");
				message.getMessageProperties().getHeaders().put("desc","额外修改的信息描述");
				message.getMessageProperties().getHeaders().put("attr","额外新增的信息");
				return message;
			}
		});
	}
	
	@Test
	public void testSendMessage2() throws Exception{
		MessageProperties messageProperties = MessagePropertiesBuilder.newInstance().build();
		messageProperties.setContentType("text/plain");
		messageProperties.getHeaders().put("type", "自定义消息类型");
		JSONObject json = new JSONObject();
		json.put("id", 1);
		json.put("name", "xiaochao");
		String messageBodyStr = json.toJSONString();
		Message message = new Message(messageBodyStr.getBytes(), messageProperties);
		rabbitTemplate.send("topicExchange01", "spring.abc", message);
		rabbitTemplate.convertAndSend("topicExchange01", "spring.amqp", messageBodyStr);
		rabbitTemplate.convertAndSend("topicExchange02", "rabbit.test", messageBodyStr);
	}



	@Test
	public void testSendMessage3() throws Exception{
		MessageProperties messageProperties = MessagePropertiesBuilder.newInstance().build();
		messageProperties.setContentType("application/json");
		messageProperties.getHeaders().put("type", "自定义消息类型");
		JSONObject json = new JSONObject();
		json.put("id", 1);
		json.put("name", "xiaochao");
		String messageBodyStr = json.toJSONString();
		Message message = new Message(messageBodyStr.getBytes(), messageProperties);
		rabbitTemplate.send("topicExchange01", "spring.abc", message);
		rabbitTemplate.convertAndSend("topicExchange01", "spring.amqp", message);
		rabbitTemplate.convertAndSend("topicExchange02", "rabbit.test", message);
	}
	
@Test
public void testSendMessage4() throws Exception{
	MessageProperties messageProperties = MessagePropertiesBuilder.newInstance().build();
	messageProperties.setContentType("application/json");
	messageProperties.getHeaders().put("__TypeId__", "dog");
//	User user = new User(1,"xiaochao",22);
	Dog user = new Dog(1,"xiaochao",22);
	String userStr = JSON.toJSONString(user);

	String messageBodyStr = userStr.toString();
	Message message = new Message(messageBodyStr.getBytes(), messageProperties);
	rabbitTemplate.send("topicExchange01", "spring.abc", message);
	rabbitTemplate.convertAndSend("topicExchange01", "spring.amqp", message);
	rabbitTemplate.convertAndSend("topicExchange02", "rabbit.test", message);
}
	
	

}