package com.me.amqp.starter.queues.listeners;


import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPMessageHandlerServiceAbstract;
import com.me.amqp.starter.utils.Utils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AMQPMessageListener extends MessageListenerAdapter  {  
    
    
    @Autowired
    Utils utils;
    
    @Autowired
    AMQPMessageHandlerServiceAbstract messageHandler;
    
    
    private AMQPServiceProperties aMQPServiceProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPMessageListener.class);
    
    @Autowired
    public AMQPMessageListener(AMQPServiceProperties aMQPServiceProperties) {
        this.aMQPServiceProperties = aMQPServiceProperties;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception{
        LOGGER.info("[Receiver] Principal received: {}", message.getBody());
        
        LOGGER.info("******************************************************************");
        LOGGER.info("TOPIC QUEUE");
        LOGGER.info("Message received at: ", message.getMessageProperties().getTimestamp());
        LOGGER.info("Message with operation header value: {}. ", utils.getOperationHeaderValueFromMessage(message.getMessageProperties().getHeaders()).getValue());
        LOGGER.info("Message with channel header value: {}. ", utils.getChannelHeaderValueFromMessage(message.getMessageProperties().getHeaders()).getValue());
        
        
        //Invoke message handler
        try{
            messageHandler.invokeHandler(message);
        }catch(NullPointerException npe){
            LOGGER.error("[Queue message handling - Listener] Error accessing to null result set from concrete service.");
        }
        LOGGER.info("******************************************************************");
    }
    

}
