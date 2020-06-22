package com.everjiankang.dependency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.everjiankang.dependency.springboot.RabbitmqSender;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootAMQPTest {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Autowired
	private RabbitmqSender rabbitmqSender;
	
	@Test
	public void testSender1() throws Exception {
		Map<String,Object> properties = new HashMap<>();
		properties.put("number", "12345");
		properties.put("send_time", sdf.format(new Date()));
		rabbitmqSender.send("Hello RabbitMQ For Sprint Boot", properties);
	}
	
}