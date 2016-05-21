package com.me.amqp.starter.queues.listeners;


import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AMQPMessageListener implements ChannelAwareMessageListener {
    
    
    @Autowired
    MessageConverter jsonMessageConverter;
 
    @Autowired
    RabbitTemplate replySender;
    
    @Autowired
    Utils utils;
    
    
    private AMQPServiceProperties aMQPServiceProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPMessageListener.class);
    
    @Autowired
    public AMQPMessageListener(AMQPServiceProperties aMQPServiceProperties){
        this.aMQPServiceProperties = aMQPServiceProperties;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception{
        LOGGER.info("[Receiver] Principal received: {}", message.getBody());
        
        LOGGER.info("******************************************************************");
        LOGGER.info("FANOUT QUEUE");
        LOGGER.info("Message received at: ", message.getMessageProperties().getTimestamp());
        LOGGER.info("Message with operation header value: {}. ", utils.getOperationHeaderValueFromMessage(message.getMessageProperties().getHeaders()).getValue());
        LOGGER.info("Message with channel header value: {}. ", utils.getChannelHeaderValueFromMessage(message.getMessageProperties().getHeaders()).getValue());
        
        
        //List<?> result = aMQPDeliveryHandlerService.handleIncomingMessage(message);
        List<?> result = null;
        try{
            if(result.size() > 0){
                result.forEach(item -> replySender.convertAndSend(item));
            }else{
                LOGGER.error("[Queue message handling - Listener] Empty result set from concrete service.");
            }
        }catch(NullPointerException npe){
            LOGGER.error("[Queue message handling - Listener] Error accessing to null result set from concrete service.");
        }
        LOGGER.info("******************************************************************");
    }
    

}
