package com.everjiankang.dependency.demo8;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

public class Producer {

    //交换机名称
    private static final String EXCHANGE_NAME = "test_exchange_fanout_message_confirm";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout", true,true,null);
        //指定消息投递模式为：消息的确认模式
        channel.confirmSelect();
        //设置confirm返回监听
        channel.addConfirmListener(new ConfirmListener() {
        	//1.处理失败场景，deliveryTag：消息的唯一标签
        	//失败场景：磁盘写满了，队列数达到上限了mq出问题了等
			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("-----------no ack------------" + deliveryTag + " | " + multiple);
			}
			//2.处理成功场景，deliveryTag：消息的唯一标签
			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("-----------ack------------" + deliveryTag + " | " + multiple);
			}
			//3.第三种情况，Ack 和No Ack都没有收到，这就需要可靠性投递来解决。假设Broker端返回的确认突然出现网络的闪断，
			//那我连ACK到底成功还是失败都不知道，那怎么办呢？用定时任务取抓取一些中间状态的消息，然后重新触发发送，补偿。
		});
        
        String message = "发送一条需要确认的消息！！！！";
        channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes());
       
        System.out.println("生产者 send ："+message);
        channel.close();
        connection.close();
        System.out.println("over");
    }
}