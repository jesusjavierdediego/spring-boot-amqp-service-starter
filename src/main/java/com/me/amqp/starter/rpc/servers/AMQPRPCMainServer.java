package com.me.amqp.starter.rpc.servers;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerServiceAbstract;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.amqp.rabbit.connection.Connection;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class AMQPRPCMainServer {

            
    private Connection connection;

    private final CountDownLatch latch = new CountDownLatch(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCMainServer.class);

    @Autowired
    public AMQPRPCMainServer(
            ConnectionFactory clientConnectionFactory, 
            AMQPRPCDeliveryHandlerServiceAbstract aMQPRPCDeliveryHandlerService, 
            AMQPServiceProperties aMQPServiceProperties) {

        try {
            connection = clientConnectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(
                    aMQPServiceProperties.getRpcqueuename(),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueisDurable()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueexclusive()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueautodelete()),
                    null
            );
            channel.exchangeDeclare(aMQPServiceProperties.getRpcexchangename(), "topic");
            channel.basicQos(1);
            
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(aMQPServiceProperties.getRpcqueuename(), Boolean.valueOf(aMQPServiceProperties.getRpcqueueautoack()), consumer);
            
            while (true) {
                
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                BasicProperties props = delivery.getProperties();
                BasicProperties replyProps = new BasicProperties.Builder()
                        .correlationId(props.getCorrelationId())
                        .build();

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    LOGGER.info("[RPC - Server] RETRIEVED MESSAGE: {}", message);
                    byte[] processedResult = aMQPRPCDeliveryHandlerService.invokeHandler(message.getBytes("UTF-8"));
                    LOGGER.info("[RPC - Server] PROCESSED MESSAGE FOR RESPONSE: {}", new String(processedResult));

                    channel.basicPublish("", props.getReplyTo(), replyProps, processedResult);
                    //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    
                    LOGGER.info("[RPC - Server] SENT MESSAGE: {}", new String(processedResult));
                } catch (Exception e) {
                    LOGGER.error(" [RPC - Server]  ERROR TRYING TO SEND THE RESPONSE: " + e.toString());
                }
                latch.countDown();
            }
            
        } catch (IOException | TimeoutException | InterruptedException | ShutdownSignalException | ConsumerCancelledException e) {
            LOGGER.error(" [RPC - Server]  ERROR TRYING TO SET THE AMQP CONNECTION:  " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    LOGGER.error(" [RPC - Server]  ERROR TRYING TO CLOSE THE CONNECTION:  " + e.toString());
                }
            }
        }
    }
}
