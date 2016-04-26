package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import java.util.UUID;
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
    
    private QueueingConsumer consumer;
    
    private String replyQueueName;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCClient.class);

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;
    
    @Autowired
    AMQPRPCMainServer rpcServer;
    
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
            
            replyQueueName = channel.queueDeclare().getQueue();
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(replyQueueName, true, consumer);

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

    public void sendRPCMessage(String payload) throws Exception {
        try {
            byte[] message = payload.getBytes("UTF-8");
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(configuration.getRpcqueuename()).build();
            channel.basicPublish(aMQPServiceProperties.getRpcexchangename(), aMQPServiceProperties.getRpcroutingKeyname(), props, message);
            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent.");
            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent in exchange: {}", aMQPServiceProperties.getRpcexchangename());
            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent in routing key: {}", aMQPServiceProperties.getRpcroutingKeyname());
            //close();
            LOGGER.info("[RPC - Client sendRPCMessage()] Connection closed.");
        } catch (IOException e) {
            LOGGER.error("[RPC - Client sendRPCMessage()] Error handling process: {}", e.getMessage());
        }

    }
    
    public String sendAndReceiveRPCMessage(
            String exchange, 
            String routingKey,
            String payload) 
            throws Exception {
        String response = null;
        try {
            byte[] message = payload.getBytes("UTF-8");
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(configuration.getRpcqueuename()).build();
            channel.basicPublish(aMQPServiceProperties.getRpcexchangename(), aMQPServiceProperties.getRpcroutingKeyname(), props, message);
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();            
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            String correlationId = UUID.randomUUID().toString();
            channel.basicConsume(exchange, null);
            
            while (true) {
                if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                  response = new String(delivery.getBody(),"UTF-8");
                  break;
                }
             }

              

            //close();
        } catch (IOException e) {
            LOGGER.error("[RPC - Client sendAndReceiveRPCMessage()] Error handling process: {}", e.getMessage());
        }
        return response;
        
    }
}
