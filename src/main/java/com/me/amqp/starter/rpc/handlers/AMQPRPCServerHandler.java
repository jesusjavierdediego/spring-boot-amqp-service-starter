package com.me.amqp.starter.rpc.handlers;

import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerServiceAbstract;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


public class AMQPRPCServerHandler extends DefaultConsumer {

    private Channel channel;


    @Value("${amqp.service.starter.rpc.exchange.name}")
    private String RPC_EXCHANGE_NAME;

    @Value("${amqp.service.starter.rpc.routingKey.name}")
    private String RPC_ROUTINGKEY_NAME;

    @Autowired
    public AMQPRPCServerHandler(Channel channel) {
        super(channel);
        this.channel = channel;
    }
    
    @Autowired
    AMQPRPCDeliveryHandlerServiceAbstract aMQPRPCDeliveryHandlerService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCServerHandler.class);

    private final CountDownLatch latch = new CountDownLatch(1);
    
    @Override
    public void handleDelivery(
            String consumerTag,
            Envelope envelope,
            AMQP.BasicProperties properties,
            byte[] body) throws IOException {
        BasicProperties replyProps = new BasicProperties.Builder()
                .correlationId(properties.getCorrelationId())
                .build();

        byte[] processedResult = aMQPRPCDeliveryHandlerService.invokeHandler(body, channel);
        
        try{
            //channel.basicPublish(RPC_EXCHANGE_NAME, RPC_ROUTINGKEY_NAME, replyProps, processedResult);
            channel.basicPublish(RPC_EXCHANGE_NAME, properties.getReplyTo(), replyProps, processedResult);
        }catch(IOException ioe){
            LOGGER.error("[RPC - Server handleDelivery()] Error handling process: {}", ioe.getMessage());
        }
        
        latch.countDown();
    }
}
       /*
            void basicPublish(java.lang.String exchange,
                java.lang.String routingKey,
                AMQP.BasicProperties props,
                byte[] body) throws java.io.IOException
Publish a message. Publishing to a non-existent exchange will result in a channel-level protocol exception, which closes the channel. Invocations of Channel#basicPublish will eventually block if a resource-driven alarm is in effect.
Parameters:
exchange - the exchange to publish the message to
routingKey - the routing key
props - other properties for the message - routing headers etc
body - the message body
Throws:
java.io.IOException - if an error is encountered
            */
