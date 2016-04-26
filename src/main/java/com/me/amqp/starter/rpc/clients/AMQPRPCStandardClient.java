package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
import com.me.amqp.starter.utils.Utils;
import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.RpcClient;
import java.util.HashMap;
import java.util.Map;

@Service
public class AMQPRPCStandardClient implements AMQPRPCClient {

    private static Connection connection;

    private static Channel channel;

    private AMQPServiceProperties configuration;

    private RpcClient rpcClient;

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;

    @Autowired
    Utils utils;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCStandardClient.class);

    
    @Autowired
    public AMQPRPCStandardClient(ConnectionFactory connectionFactory, AMQPServiceProperties aMQPServiceProperties) {
        this.aMQPServiceProperties = aMQPServiceProperties;
        configuration = aMQPServiceProperties;

        connection = connectionFactory.createConnection();
        LOGGER.info("[RPC - StandardClient sendRPCMessage()] Connection created.");
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

            rpcClient = new RpcClient(
                    channel,
                    aMQPServiceProperties.getRpcexchangename(),
                    aMQPServiceProperties.getRpcroutingKeyname(),
                    Integer.valueOf(aMQPServiceProperties.getRPCResponseTimeout())
            );
            rpcClient.checkConsumer();

            LOGGER.info("[AMQP-service] Ready to send RPC requests in channel {}", channel.getChannelNumber());
        } catch (IOException ioe) {
            LOGGER.error("[RPC - StandardClient Constructor] Error handling process: {}", ioe.getMessage());
        }
    }

    public void close() throws Exception {
        try {
            connection.close();
        } catch (NullPointerException npe) {
            LOGGER.error("[RPC - StandardClient close()] No connection to be closed: {}", npe.getMessage());
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

            rpcClient.publish(props, message);

            LOGGER.info("[RPC - StandardClient sendRPCMessage()] Message successfully sent.");
//            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent in exchange: {}", aMQPServiceProperties.getRpcexchangename());
//            LOGGER.info("[RPC - Client sendRPCMessage()] Message successfully sent in routing key: {}", aMQPServiceProperties.getRpcroutingKeyname());
//            close();
//            LOGGER.info("[RPC - Client sendRPCMessage()] Connection closed.");
        } catch (IOException e) {
            LOGGER.error("[RPC - StandardClient sendRPCMessage()] Error handling process: {}", e.getMessage());
        }

    }

    @Override
    public String sendAndReceiveRPCMessage(String payload) throws Exception {
        String response = null;
        try {
            byte[] message = payload.getBytes("UTF-8");
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(configuration.getRpcqueuename()).build();
            response = new String(rpcClient.primitiveCall(props, message));
            LOGGER.info("[RPC - Client sendAndReceiveRPCMessage()] Message successfully sent.");
            //close();
        } catch (IOException e) {
            LOGGER.error("[RPC - StandardClient sendAndReceiveRPCMessage()] Error handling process: {}", e.getMessage());
            return configuration.getRpcmessagedefaulterror();
        }
        return response;

    }
    
    @Override
    public Map<String, Object> sendAndReceiveMapRPCMessage(Map<String, Object> payload) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(configuration.getRpcqueuename()).build();
            result = rpcClient.mapCall(payload);
            LOGGER.info("[RPC - Client sendAndReceiveMapRPCMessage()] Message successfully sent.");
            //close();
        } catch (IOException e) {
            LOGGER.error("[RPC - Client sendAndReceiveMapRPCMessage()] Error handling process: {}", e.getMessage());
            result.put("1", configuration.getRpcmessagedefaulterror());
        }
        return result;

    }
}
