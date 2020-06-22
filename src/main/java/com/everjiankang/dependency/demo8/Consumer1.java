package com.everjiankang.dependency.demo8;

import java.io.IOException;

import com.everjiankang.dependency.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Consumer1 {

    private static final String QUEUE_NAME = "test_queue_exchange_1";
    private static final String EXCHANGE_NAME = "test_exchange_fanout_message_confirm";
    private static final String ROUTING_KEY_NAME = "";

    public static void main(String[] args) throws IOException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"fanout", true,true,null);
        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //绑定队列到交换机（这个交换机名称一定要和生产者的交换机名相同）
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY_NAME);

        //同一时刻服务器只会发一条数据给消费者
        channel.basicQos(0, 1, false);
//        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                String message = new String(body,"UTF-8");
                System.out.println("收到消息："+message);
                
                boolean multiple = false; //是否批量签收
                //这个方法会主动回送给Broker一个应答，表示这条消息我已经处理完了，你可以再给我下一条了
                channel.basicAck(envelope.getDeliveryTag(),multiple); 
                
                boolean requeue = false; //是否重回队列
                channel.basicNack(envelope.getDeliveryTag(), multiple, requeue);
            }
        };
        boolean autoAck = false; //不自动确认
        channel.basicConsume(QUEUE_NAME,autoAck,consumer);
    }

}