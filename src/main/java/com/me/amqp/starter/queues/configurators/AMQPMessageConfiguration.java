package com.me.amqp.starter.queues.configurators;

import com.me.amqp.starter.queues.listeners.AMQPMessageListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class AMQPMessageConfiguration {

   
    
    @Autowired
    private CachingConnectionFactory rabbitConnectionFactory;
    
    
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    //QUEUES
    //Listening to:
    @Bean
    Queue listenQueue(AMQPServiceProperties aMQPServiceProperties) {
        return new Queue(
                aMQPServiceProperties.getQueuename(), 
                Boolean.valueOf(aMQPServiceProperties.getQueueisDurable()
                ));
    }
    //Send replies to:
    @Bean
    Queue replyQueue(AMQPServiceProperties aMQPServiceProperties) {
        return new Queue(
                aMQPServiceProperties.getReplyqueuename(), 
                Boolean.valueOf(aMQPServiceProperties.getReplyqueueisDurable()
                ));
    }

    @Bean
    SimpleMessageListenerContainer listenContainer(CachingConnectionFactory connectionFactory, AMQPServiceProperties aMQPServiceProperties) {
        final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setQueueNames(aMQPServiceProperties.getQueuename());
        listenerContainer.setMessageConverter(jsonMessageConverter());
        listenerContainer.setMessageListener(listener(aMQPServiceProperties));
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return listenerContainer;
    }

    @Bean
    AMQPMessageListener listener(AMQPServiceProperties aMQPServiceProperties) {
        return new AMQPMessageListener(aMQPServiceProperties);
    }

    @Bean
    @Qualifier("REPLYQUEUE")
    @Primary
    public RabbitTemplate replySender(AMQPServiceProperties aMQPServiceProperties) {
        RabbitTemplate template = new RabbitTemplate(this.rabbitConnectionFactory);
        template.setQueue(aMQPServiceProperties.getReplyqueuename());
        template.setExchange(aMQPServiceProperties.getReplyexchangename());
        template.setRoutingKey(aMQPServiceProperties.getReplyroutingKeyname());
        template.setReplyQueue(this.listenQueue(aMQPServiceProperties));
        template.setReplyTimeout(Integer.valueOf(aMQPServiceProperties.getReplytimeout()));
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

   
}
