package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
import com.me.amqp.starter.utils.Utils;
import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCSimpleClient implements AMQPRPCClient{

    private static Connection connection;
    
    private static Channel channel;

    private AMQPServiceProperties configuration;
    
    private QueueingConsumer consumer;
    
    private String replyQueueName; 

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;
    
    @Autowired
    AMQPRPCMainServer rpcServer;
    
    @Autowired
    Utils utils;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCSimpleClient.class);
    
    @Autowired
    public AMQPRPCSimpleClient(ConnectionFactory connectionFactory, AMQPServiceProperties aMQPServiceProperties) {
        this.aMQPServiceProperties = aMQPServiceProperties;
        configuration = aMQPServiceProperties;
        
        connection = connectionFactory.createConnection();
        LOGGER.info("[RPC - SimpleClient sendRPCMessage()] Connection created.");
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
            LOGGER.error("[RPC - SimpleClient Constructor] Error handling process: {}", ioe.getMessage());
        }
    }

    public void close() throws Exception {
        try {
            connection.close();
        } catch (NullPointerException npe) {
            LOGGER.error("[RPC - SimpleClient close()] No connection to be closed: {}", npe.getMessage());
        }
    }

    @Override
    public void sendRPCMessage(String operationHeader, String channelHeader, String payload) throws Exception {
        try {
            byte[] message = payload.getBytes("UTF-8");
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder()
                    .replyTo(configuration.getRpcqueuename())
                    .headers(utils.getMessagePropertiesFromList(operationHeader, channelHeader))
                    .build();

            channel.basicPublish(
                    aMQPServiceProperties.getRpcexchangename(), 
                    aMQPServiceProperties.getRpcroutingKeyname(), 
                    props, 
                    message
            );
            
            LOGGER.info("[RPC - SimpleClient sendRPCMessage()] Message successfully sent.");
            LOGGER.info("[RPC - SimpleClient sendRPCMessage()] Message successfully sent in exchange: {}", aMQPServiceProperties.getRpcexchangename());
            LOGGER.info("[RPC - SimpleClient sendRPCMessage()] Message successfully sent in routing key: {}", aMQPServiceProperties.getRpcroutingKeyname());
            //close();
            LOGGER.info("[RPC - SimpleClient sendRPCMessage()] Connection closed.");
        } catch (IOException e) {
            LOGGER.error("[RPC - SimpleClient sendRPCMessage()] Error handling process: {}", e.getMessage());
        }

    }
    
    @Override
    public String sendAndReceiveRPCMessage(String payload) throws Exception {
        String response = null;
        try {
            byte[] message = payload.getBytes("UTF-8");
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(configuration.getRpcqueuename()).build();
            channel.basicPublish(aMQPServiceProperties.getRpcexchangename(), aMQPServiceProperties.getRpcroutingKeyname(), props, message);
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();            
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            String correlationId = UUID.randomUUID().toString();
            channel.basicConsume(aMQPServiceProperties.getRpcexchangename(), consumer);
            
            while (true) {
                if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                  response = new String(delivery.getBody(),"UTF-8");
                  break;
                }
             }
            //close();
        } catch (IOException e) {
            LOGGER.error("[RPC - SimpleClient sendAndReceiveRPCMessage()] Error handling process: {}", e.getMessage());
            return configuration.getRpcmessagedefaulterror();
        }
        return response;
        
    }

    @Override
    public Map<String, Object> sendAndReceiveMapRPCMessage(Map<String, Object> payload) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
