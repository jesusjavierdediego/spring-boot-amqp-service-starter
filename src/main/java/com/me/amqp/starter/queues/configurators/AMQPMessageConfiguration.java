package com.me.amqp.starter.queues.configurators;

import com.me.amqp.starter.queues.listeners.AMQPMessageListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class AMQPMessageConfiguration {

    @Value("${amqp.listen.queue.name}")
    private String LISTEN_QUEUE_NAME;
    
    @Value("${amqp.listen.queue.isDurable}")
    private Boolean LISTEN_QUEUE_ISDURABLE;

    @Value("${amqp.listen.reply.timeout}")
    private Long LISTEN_REPLY_TIMEOUT;
    
    @Value("${amqp.reply.queue.name}")
    private String REPLY_QUEUE_NAME;
    
    @Value("${amqp.reply.queue.isDurable}")
    private Boolean REPLY_QUEUE_ISDURABLE;

    @Value("${amqp.reply.exchange.name}")
    private String REPLY_EXCHANGE_NAME;

    @Value("${amqp.reply.routingKey.name}")
    private String REPLY_ROUTINGKEY_NAME;

    @Autowired
    private ConnectionFactory rabbitConnectionFactory;
    
    
    
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    //QUEUES
    //Listening to:
    @Bean
    Queue listenQueue() {
        return new Queue(LISTEN_QUEUE_NAME, LISTEN_QUEUE_ISDURABLE);
    }
    //Send replies to:
    @Bean
    Queue replyQueue() {
        return new Queue(REPLY_QUEUE_NAME, REPLY_QUEUE_ISDURABLE);
    }

    @Bean
    SimpleMessageListenerContainer listenContainer(ConnectionFactory connectionFactory) {
        final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setQueueNames(LISTEN_QUEUE_NAME);
        listenerContainer.setMessageConverter(jsonMessageConverter());
        listenerContainer.setMessageListener(listener());
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return listenerContainer;
    }

    @Bean
    AMQPMessageListener listener() {
        return new AMQPMessageListener();
    }

    @Bean
    @Qualifier("REPLYQUEUE")
    @Primary
    public RabbitTemplate replySender() {
        RabbitTemplate template = new RabbitTemplate(this.rabbitConnectionFactory);
        template.setQueue(REPLY_QUEUE_NAME);
        template.setExchange(REPLY_EXCHANGE_NAME);
        template.setRoutingKey(REPLY_ROUTINGKEY_NAME);
        template.setReplyQueue(this.listenQueue());
        template.setReplyTimeout(LISTEN_REPLY_TIMEOUT);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

   
}
