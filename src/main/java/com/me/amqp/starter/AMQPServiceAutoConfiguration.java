package com.me.amqp.starter;

import com.me.amqp.starter.queues.configurators.AMQPRPCConfiguration;
import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.queues.listeners.AMQPMessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "amqp.service", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(AMQPServiceProperties.class)
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class AMQPServiceAutoConfiguration {

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;

    @Autowired
    CachingConnectionFactory connectionFactory;

   

    @Bean
    public AMQPRPCConfiguration aMQPRPCConfiguration() {
        return new AMQPRPCConfiguration();
    }
    
    @Bean
    public AMQPMessageListener aMQPMessageListener() {
        return new AMQPMessageListener(aMQPServiceProperties);
    }
    
//    @Bean
//    public AMQPTopicConfiguration aMQPTopicConfiguration() throws Exception {
//        return new AMQPTopicConfiguration(aMQPServiceProperties);
//    }
}
