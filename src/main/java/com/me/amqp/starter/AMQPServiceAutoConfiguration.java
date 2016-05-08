package com.me.amqp.starter;

import com.me.amqp.starter.queues.configurators.AMQPMessageConfiguration;
import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.queues.listeners.AMQPMessageListener;
import com.me.amqp.starter.rpc.clients.AMQPRPCSimpleClient;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
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
        
        

        @Bean
	public AMQPRPCMainServer aMQPRPCMainServer() throws Exception{
		return new AMQPRPCMainServer();
	}
        
        @Bean
	public AMQPRPCSimpleClient aMQPRPCClient() throws Exception{
		return new AMQPRPCSimpleClient();
	}
        
        @Bean
	public AMQPMessageConfiguration aMQPMessageConfiguration() {
		return new AMQPMessageConfiguration();
	}
        
        @Bean
	public AMQPMessageListener aMQPMessageListener() {
		return new AMQPMessageListener(aMQPServiceProperties);
	}      
}
