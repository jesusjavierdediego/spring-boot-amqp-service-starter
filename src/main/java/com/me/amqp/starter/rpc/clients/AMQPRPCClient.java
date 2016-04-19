package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import java.io.IOException;
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
    AMQPServiceProperties aMQPServiceProperties;
    
    @Autowired
    public AMQPRPCClient(ConnectionFactory connectionFactory, AMQPServiceProperties aMQPServiceProperties) {
        this.aMQPServiceProperties = aMQPServiceProperties;
        configuration = aMQPServiceProperties;
        
        connection = connectionFactory.createConnection();
        LOGGER.info("[RPC - Client sendRPCMessage()] Connection created.");
        try {
            channel = connectionFactory.createConnection().createChannel(true);
            channel.queueDeclare(
                    aMQPServiceProperties.getRpcqueuename(),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueisDurable()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueexclusive()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueautodelete()),
                    null);
            channel.exchangeDeclare(aMQPServiceProperties.getRpcexchangename(), "topic");

            channel.basicQos(1);

            LOGGER.info("[AMQP-service] Ready to send RPC requests in channel {}", channel.getChannelNumber());
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

    public void sendRPCMessage(byte[] message) throws Exception {
        try {

            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(configuration.getRpcqueuename()).build();
            channel.basicPublish(aMQPServiceProperties.getRpcexchangename(), aMQPServiceProperties.getRpcroutingKeyname(), props, message);
            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent.");
            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent in exchange: {}", aMQPServiceProperties.getRpcexchangename());
            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent in routing key: {}", aMQPServiceProperties.getRpcroutingKeyname());
            close();
            LOGGER.info("[RPC - Client sendRPCMessage()] Connection closed.");
        } catch (IOException e) {
            LOGGER.error("[RPC - Client sendRPCMessage()] Error handling process: {}", e.getMessage());
        }

    }
}
