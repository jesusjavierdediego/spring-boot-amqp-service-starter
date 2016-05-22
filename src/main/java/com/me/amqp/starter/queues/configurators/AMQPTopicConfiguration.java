package com.me.amqp.starter.queues.configurators;

import com.me.amqp.starter.queues.listeners.AMQPMessageListener;
import com.me.amqp.starter.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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

@Configuration
@EnableAutoConfiguration
public class AMQPTopicConfiguration {

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;

    @Autowired
    Utils utils;

    @Autowired
    CachingConnectionFactory rabbitConnectionFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPTopicConfiguration.class);

    @Bean
    @Qualifier("topicRabbitTemplate")
    public RabbitTemplate topicRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setQueue(aMQPServiceProperties.getTopicQueueName());
        template.setExchange(aMQPServiceProperties.getTopicExchangeName());
        template.setRoutingKey(aMQPServiceProperties.getTopicBindingName());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    Queue topicQueue() {
        return new Queue(
                aMQPServiceProperties.getTopicQueueName(),
                Boolean.valueOf(aMQPServiceProperties.getTopicQueueisDurable()),
                Boolean.valueOf(aMQPServiceProperties.getTopicQueueIsExclusive()),
                Boolean.valueOf(aMQPServiceProperties.getTopicQueueIsAutodelete())
        );
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(aMQPServiceProperties.getTopicExchangeName());
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(topicQueue()).to(exchange()).with(aMQPServiceProperties.getTopicBindingName());
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory rabbitConnectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory);
        container.setQueues(topicQueue());
        container.setMessageListener(listenerAdapter);
        container.setMessageConverter(jsonMessageConverter());
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }

    @Bean
    AMQPMessageListener listenerAdapter(AMQPServiceProperties aMQPServiceProperties) {
        return new AMQPMessageListener(aMQPServiceProperties);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
