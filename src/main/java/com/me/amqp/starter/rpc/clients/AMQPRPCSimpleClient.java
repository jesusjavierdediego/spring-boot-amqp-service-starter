package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
import com.me.amqp.starter.utils.Utils;
import java.io.IOException;
import com.rabbitmq.client.AMQP;
//import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.connection.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCSimpleClient implements AMQPRPCClient {

    private static Connection connection;

    private static Channel channel;

    private AMQPServiceProperties configuration;

    private QueueingConsumer consumer;

    private String replyQueueName;

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;

    @Autowired
    @Qualifier("AMQPRPCMainServer")
    AMQPRPCMainServer rpcServer;

    @Autowired
    Utils utils;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCSimpleClient.class);

    @Autowired
    public AMQPRPCSimpleClient(
            ConnectionFactory clientConnectionFactory, 
            AMQPServiceProperties aMQPServiceProperties) {
        
        this.aMQPServiceProperties = aMQPServiceProperties;
        configuration = aMQPServiceProperties;

        try {
            connection = clientConnectionFactory.newConnection();
            LOGGER.info("[RPC - SimpleClient sendRPCMessage()] Connection created.");
            channel = connection.createChannel();
            channel.queueDeclare(
                    aMQPServiceProperties.getRpcqueuename(),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueisDurable()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueexclusive()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueautodelete()),
                    null);
            channel.exchangeDeclare(aMQPServiceProperties.getRpcexchangename(), "topic");

            channel.basicQos(10);

            replyQueueName = channel.queueDeclare().getQueue();
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(replyQueueName, true, consumer);

            LOGGER.info("[AMQP-service] Ready to send RPC requests in channel {}", channel.getChannelNumber());
        } catch (IOException | TimeoutException e) {
            LOGGER.error("[RPC - SimpleClient Constructor] Error handling process: {}", e.getMessage());
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
            String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder()
                    .replyTo(configuration.getRpcqueuename())
                    .correlationId(corrId)
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
            //LOGGER.info("[RPC - SimpleClient sendRPCMessage()] Connection closed.");
        } catch (IOException e) {
            LOGGER.error("[RPC - SimpleClient sendRPCMessage()] Error handling process: {}", e.getMessage());
        }

    }

    @Override
    public String sendAndReceiveRPCMessage(String payload) throws Exception {
        String response = null;
        try {
            byte[] message = payload.getBytes("UTF-8");
            String correlationId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().correlationId(correlationId).replyTo(replyQueueName).build();
            //channel.basicPublish(aMQPServiceProperties.getRpcexchangename(), aMQPServiceProperties.getRpcroutingKeyname(), props, message);
            channel.basicPublish("", aMQPServiceProperties.getRpcqueuename(), props, message);
            //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                    response = new String(delivery.getBody(), "UTF-8");
                    break;
                }
            }
            //close();
        } catch (IOException e) {
            LOGGER.error("[RPC - SimpleClient sendAndReceiveRPCMessage()] Error handling process: {}", e.getMessage());
            return configuration.getRpcmessagedefaulterror();
        } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException e) {
            LOGGER.error("[RPC - SimpleClient sendAndReceiveRPCMessage()] Internal Error handling process: {}", e.getMessage());
            return configuration.getRpcmessagedefaulterror();
        }

        return response;

    }

    @Override
    public Map<String, Object> sendAndReceiveMapRPCMessage(Map<String, Object> payload) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
