package com.me.amqp.starter.queues.listeners;


import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPMessageHandlerServiceAbstract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Map.Entry;
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
    AMQPMessageHandlerServiceAbstract messageHandler;
    
    @Autowired
    RabbitTemplate replySender;
    
    
    private AMQPServiceProperties configuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPMessageListener.class);
    
    @Autowired
    public AMQPMessageListener(AMQPServiceProperties aMQPServiceProperties){
        configuration = aMQPServiceProperties;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception{
        LOGGER.info("[Receiver] Principal received: {}", message.getBody());
        
        /*
         * Getting information from headers
         * 1-Channel
         * 2-Operation type
        */
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        Entry opHeader = headers.entrySet().parallelStream().filter(e -> e.getKey().equalsIgnoreCase(configuration.getOperationheader())).findFirst().get();
        Entry clientChannel = headers.entrySet().parallelStream().filter(e -> e.getKey().equalsIgnoreCase(configuration.getChannelheader())).findFirst().get();
        
        LOGGER.info("******************************************************************");
        LOGGER.info("Message received at: ", message.getMessageProperties().getTimestamp());
        LOGGER.info("Message for operation: {}. ", opHeader.getValue());
        LOGGER.info("Message in channel: {}. ", clientChannel.getValue());
        
        //Invoke message handler
        List<?> result = messageHandler.invokeHandler(message);
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
