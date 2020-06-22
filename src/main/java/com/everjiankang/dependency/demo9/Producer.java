package com.everjiankang.dependency.demo9;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;

public class Producer {
    
    private static final String EXCHANGE_NAME = "test_exchange_fanout_message_confirm";//交换机名称

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout", true,true,null);
        //设置消息发送后匹配失败时的处理方法
        channel.addReturnListener(new ReturnListener() {
			@Override
			public void handleReturn(int replyCode,String replyText,String exchange,
								String routingKey,AMQP.BasicProperties properties,byte[] body)throws IOException {
				System.out.println("-----------ReturnListener start ------------");
				System.out.println(replyCode + " | " + replyText  + " | " +  exchange  + " | " +  routingKey +  " | " +  properties + " | " + new String(body));
				System.out.println("-----------ReturnListener end ------------");
			}
		});
        String message = "发送一条需要确认的消息！！！！";
        
        boolean mandatory = true; //[ˈmændətəri] adj.强制的; 法定的; 义务的; n.	受托者;   
        channel.basicPublish(EXCHANGE_NAME, "", mandatory, null, message.getBytes());
        System.out.println("生产者 send ："+message);
        channel.close();
        connection.close();
        System.out.println("over");
    }
}