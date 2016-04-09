package com.me.amqp.starter.rpc.clients;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCClient {

    @Value("${amqp.service.starter.rpc.queue.name}")
    private String RPC_QUEUE_NAME;

    @Value("${amqp.service.starter.rpc.queue.isDurable}")
    private Boolean RPC_QUEUE_ISDURABLE;

    @Value("${amqp.service.starter.rpc.queue.exclusive}")
    private Boolean RPC_QUEUE_EXCLUSIVE;

    @Value("${amqp.service.starter.rpc.queue.autodelete}")
    private Boolean RPC_QUEUE_AUTODELETE;

    @Value("${amqp.service.starter.rpc.queue.autoack}")
    private Boolean RPC_QUEUE_AUTOACK;

    @Autowired
    private ConnectionFactory rabbitConnectionFactory;

    private static Connection connection;
    
    private static Channel channel;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCClient.class);

    public AMQPRPCClient() {
        connection = rabbitConnectionFactory.createConnection();
        try {
            channel = rabbitConnectionFactory.createConnection().createChannel(true);
            channel.queueDeclare(
                    RPC_QUEUE_NAME,
                    RPC_QUEUE_ISDURABLE,
                    RPC_QUEUE_EXCLUSIVE,
                    RPC_QUEUE_AUTODELETE,
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

            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(RPC_QUEUE_NAME).build();
            channel.basicPublish("", RPC_QUEUE_NAME, props, message);
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("[RPC - Client sendRPCMessage()] Error handling process: {}", e.getMessage());
        }

    }
}
