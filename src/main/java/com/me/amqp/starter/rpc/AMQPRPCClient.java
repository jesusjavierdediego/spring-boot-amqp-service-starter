package com.me.amqp.starter.rpc;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.utils.Utils;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCClient {

    private final AMQPServiceProperties AMQPSERVICEPROPERTIES;

    @Autowired
    private RabbitTemplate fixedReplyQRabbitTemplate;

    @Autowired
    private DirectExchange exchange;

    @Autowired
    Utils utils;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCClient.class);

    @Autowired
    public AMQPRPCClient(AMQPServiceProperties aMQPServiceProperties) {
        this.AMQPSERVICEPROPERTIES = aMQPServiceProperties;
    }

    public String sendSomething(String message, String operationHeader, String channelHeader) throws Exception {
        LOGGER.info("Received request message: {}", message);
        String corrId = UUID.randomUUID().toString();
        Date date= new Date();
        return new String((byte[]) fixedReplyQRabbitTemplate.convertSendAndReceive(MessageBuilder.withBody(message.getBytes())
                .setReplyTo(this.AMQPSERVICEPROPERTIES.getRpcReplyQueueName())
                .setContentEncoding(this.AMQPSERVICEPROPERTIES.getDefaultEncoding())
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setHeader(this.AMQPSERVICEPROPERTIES.getOperationHeader(), operationHeader)
                .setHeader(this.AMQPSERVICEPROPERTIES.getChannelHeader(), channelHeader)
                .setTimestamp(new Timestamp(date.getTime()))
                .setCorrelationId(corrId.getBytes())
                .build()));
    }

}
