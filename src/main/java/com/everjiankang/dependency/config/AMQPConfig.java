package com.everjiankang.dependency.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.everjiankang.dependency.model.Dog;
import com.everjiankang.dependency.model.User;

@Configuration
public class AMQPConfig {
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("localhost");
		connectionFactory.setUsername("xiaochao");
		connectionFactory.setPassword("root");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}
	
	/** RabbitAdmin底层实现是从Spring容器中获取Exchange、Binding、RoutingKey以及Queue的@Bean声明 */
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		//此处必须设置为true，否则Spring容器不会加载RabbitAdmin类
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}
	
@Bean
public SimpleMessageListenerContainer simleMessageListenerContainer(ConnectionFactory connectionFactory) {
	SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
	container.setQueueNames("queue01","queue02","queue03");
	//container.setQueues(queue01(),queue02(),queue03());
	container.setMaxConcurrentConsumers(5);
	container.setConcurrentConsumers(1);
	//rejected:v.	拒绝接受; 不予考虑; 拒收; 不录用; 拒绝接纳; (因质量差) 不用，不出售，不出版;
	//默认消息被拒绝了后是否重回队列
	container.setDefaultRequeueRejected(false); 
	container.setAcknowledgeMode(AcknowledgeMode.AUTO);
	container.setConsumerTagStrategy(new ConsumerTagStrategy() {
		@Override
		public String createConsumerTag(String queue) {
			return queue + "_RCM大帅哥超帅";
		}
	});
	
	
	/**
	//1. 自定义实现消息监听
	container.setMessageListener(new ChannelAwareMessageListener() {
		@Override
		public void onMessage(Message message, Channel channel) throws Exception {
			String msg = new String(message.getBody());
			System.out.println("----ChannelAwareMessageListener_在监听：" + msg + "-----");
		}
	});
		
	//	delegate 美[ˈdelɪɡət , ˈdelɪɡeɪt]
	//n. 代表; 会议代表;
	//v. 授(权); 把(工作、权力等)委托(给下级); 选派(某人做某事);
	//2. 使用适配器方式替代自定义的监听实现
	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
	//自定义method，不设置此项默认走MessageDelegate类的handleMessage方法
	adapter.setDefaultListenerMethod("consumeMessage"); 
	//RabbitMQ默认都是byte[]类型的数据，想在适配器的方法中按照其它方式处理，需要定义转换类
	adapter.setMessageConverter(new MyMessageConverter());
	//adapter取代ChannelAwareMessageListener
	container.setMessageListener(adapter);

	
	//3.指定队列名（标签名）和适配器的方法名的映射关系
	Map<String, String> queueOrTagToMethodName = new HashMap();
	queueOrTagToMethodName.put("queue01", "method1");
	queueOrTagToMethodName.put("queue02", "method2");
	adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);

	//4. 发送json类型的数据
	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
	adapter.setDefaultListenerMethod("jsonHandleMethod");
	adapter.setMessageConverter(new Jackson2JsonMessageConverter());
	container.setMessageListener(adapter);  //adapter取代ChannelAwareMessageListener
*/	
	/**
	//5. 发送Java类型的数据
	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
	adapter.setDefaultListenerMethod("javaObjectHandleMethod");
	//依然沿用json的那个converter
	Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
	//为其添加一个Java类型映射处理类
	
	DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
	javaTypeMapper.addTrustedPackages("com.everjiankang.dependency.model");
	jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
	adapter.setMessageConverter(jackson2JsonMessageConverter);
	
	container.setMessageListener(adapter);  //adapter取代ChannelAwareMessageListener
	*/	
	
	//1.3 DefaultJackson2JavaTypeMapper 和 Jackson2JsonMessageConverter 支持java对象多映射转换
	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
	adapter.setDefaultListenerMethod("javaObjectHandleMethod");
	Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
	DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
	Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();    
	idClassMapping.put("user", User.class);
	idClassMapping.put("dog", Dog.class);
	javaTypeMapper.addTrustedPackages("com.everjiankang.dependency.model");
	javaTypeMapper.setIdClassMapping(idClassMapping);
	jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
	adapter.setMessageConverter(jackson2JsonMessageConverter);
	container.setMessageListener(adapter);
	return container;
}




	
/**
 * 自定义委托类
 */
class MessageDelegate{
	public void handleMessage(byte[] messageBody) {	//默认方法，根据MessageListenerAdapter类的源码
		System.err.println("默认方法，消息内容：" + new String(messageBody));
	}
	public void consumeMessage(byte[] messageBody) { //自定义method的方法
		System.err.println("字节数组方法，消息内容：" + new String(messageBody));
	}
	public void consumeMessage(String messageBody) {	//设置adapter.setMessageConverter(new MyMessageConverter());所走的方法
		System.err.println("字符串方法，消息内容："+ messageBody);
	}
	public void method1(String messageBody) {
		System.err.println("method1，消息内容：" + new String(messageBody));
	}
	public void method2(String messageBody) {
		System.err.println("method2，消息内容：" + new String(messageBody));
	}
	
	//方法参数一定是Map类型,而且不能有泛型，否则报错,map接收其属性
	public void jsonHandleMethod(Map message) { 
	    System.err.println("Jackson2JsonMessageConverter : " + message);
	}
	
	public void javaObjectHandleMethod(User user) {
		System.err.println("JavaTypeMapper for User: " + user);
	}
	public void javaObjectHandleMethod(Dog dog) {
		System.err.println("JavaTypeMapper for Dog : " + dog);
	}
}
	
class MyMessageConverter implements MessageConverter{
	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		//Convert a Java object to a Message.
		return new Message(object.toString().getBytes(),messageProperties);
	}
	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		// Convert from a Message to a Java object.
		String contentType = message.getMessageProperties().getContentType();
		if(null != contentType && contentType.contains("text"))
			return new String(message.getBody());
		return message.getBody();
	}
}
}