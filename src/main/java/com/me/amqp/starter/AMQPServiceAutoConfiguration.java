package com.me.amqp.starter;

import com.me.amqp.starter.queues.configurators.AMQPMessageConfiguration;
import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.queues.listeners.AMQPMessageListener;
import com.me.amqp.starter.rpc.clients.AMQPRPCSimpleClient;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerServiceAbstract;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan({"com.me.amqp.starter", "com.rabbitmq"})
@ConditionalOnProperty(prefix = "amqp.service", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(AMQPServiceProperties.class)
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class AMQPServiceAutoConfiguration {
    
    
        @Autowired
	AMQPServiceProperties aMQPServiceProperties;
        
        @Autowired
        AMQPRPCDeliveryHandlerServiceAbstract aMQPRPCDeliveryHandlerService;
        
        @Bean
	public ConnectionFactory clientConnectionFactory() throws Exception{
		return new ConnectionFactory();
	}
        
        @Bean
	public AMQPRPCMainServer aMQPRPCMainServer() throws Exception{
		return new AMQPRPCMainServer(clientConnectionFactory(), aMQPRPCDeliveryHandlerService, aMQPServiceProperties);
	}
        
        @Bean
	public AMQPRPCSimpleClient aMQPRPCClient() throws Exception{
		return new AMQPRPCSimpleClient(clientConnectionFactory(), aMQPServiceProperties);
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
