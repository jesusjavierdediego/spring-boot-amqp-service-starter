package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCClient{

    private static Connection connection;
    
    private static Channel channel;

    private AMQPServiceProperties configuration;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCClient.class);

    @Autowired
    public AMQPRPCClient(ConnectionFactory connectionFactory, AMQPServiceProperties aMQPServiceProperties) {
        
        configuration = aMQPServiceProperties;
        
        connection = connectionFactory.createConnection();
        try {
            channel = connectionFactory.createConnection().createChannel(true);
            channel.queueDeclare(
                    aMQPServiceProperties.getRpcqueuename(),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueisDurable()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueexclusive()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueautodelete()),
                    null);

            channel.basicQos(1);

            LOGGER.info("[AMQP-service] Ready to send RPC requests in channel");
        } catch (IOException ioe) {
            LOGGER.error("[RPC - Client Constructor] Error handling process: {}", ioe.getMessage());
        }
    }

    public void close() throws Exception {
        try {
            connection.close();
        } catch (NullPointerException npe) {
            LOGGER.error("[RPC - Client close()] No connection to be closed: {}", npe.getMessage());
        }
    }

    public void sendRPCMessage( byte[] message) throws Exception {
        try {
            final CountDownLatch latch = new CountDownLatch(1);

            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(configuration.getRpcqueuename()).build();
            channel.basicPublish("", configuration.getRpcqueuename(), props, message);
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("[RPC - Client sendRPCMessage()] Error handling process: {}", e.getMessage());
        }

    }
}
