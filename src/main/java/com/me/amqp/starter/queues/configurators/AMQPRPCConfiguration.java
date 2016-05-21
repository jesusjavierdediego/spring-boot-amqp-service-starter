package com.me.amqp.starter.queues.configurators;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerService;
import com.me.amqp.starter.utils.Utils;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

@Configuration
@EnableAutoConfiguration
public class AMQPRPCConfiguration {

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;

    @Autowired
    Utils utils;

    @Autowired
    CachingConnectionFactory rabbitConnectionFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCConfiguration.class);

    @Bean
    Queue queue() {
        return new Queue(
                aMQPServiceProperties.getRpcQueueName(),
                Boolean.valueOf(aMQPServiceProperties.getRpcQueueIsDurable()),
                Boolean.valueOf(aMQPServiceProperties.getRpcQueueIsexclusive()),
                Boolean.valueOf(aMQPServiceProperties.getRpcQueueIsautodelete())
        );
    }

    @Bean
    public Queue replyQueue() {
        return new Queue(
                aMQPServiceProperties.getRpcReplyQueueName(),
                Boolean.valueOf(aMQPServiceProperties.getRpcQueueIsDurable()),
                Boolean.valueOf(aMQPServiceProperties.getRpcQueueIsexclusive()),
                Boolean.valueOf(aMQPServiceProperties.getRpcQueueIsautodelete())
        );
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(aMQPServiceProperties.getRpcExchangeName(), true, false);
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(aMQPServiceProperties.getRpcBindingName());
    }

    @Bean
    @Qualifier("fixedReplyQRabbitTemplate")
    public RabbitTemplate fixedReplyQRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setQueue(queue().getName());
        template.setExchange(aMQPServiceProperties.getRpcExchangeName());
        template.setRoutingKey(aMQPServiceProperties.getRpcBindingName());
        template.setReplyQueue(replyQueue());
        template.setReplyTimeout(Long.valueOf(aMQPServiceProperties.getReplytimeout()));
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleMessageListenerContainer replyListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory);
        container.setQueues(replyQueue());
        container.setMessageListener(fixedReplyQRabbitTemplate());
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer serviceListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory);
        container.setQueues(queue());
        container.setMessageListener(new MessageListenerAdapter(aMQPRPCDeliveryHandlerService()));
        container.setMessageConverter(jsonMessageConverter());
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AMQPRPCDeliveryHandlerService aMQPRPCDeliveryHandlerService() {
        try {
            Class<?> handlerClass = ClassUtils.forName(aMQPServiceProperties.getRpcHandlerClassName(), getClass().getClassLoader());
            Object handlerObject = handlerClass.newInstance();
            return (AMQPRPCDeliveryHandlerService) handlerObject;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Error trying to get handler class instance: {}", e.getMessage());
            return null;
        }
    }

}
