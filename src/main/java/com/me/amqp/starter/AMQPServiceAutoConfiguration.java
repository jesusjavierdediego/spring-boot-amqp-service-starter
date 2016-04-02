package com.me.amqp.starter;

import com.me.amqp.starter.queues.configurators.AMQPMessageConfiguration;
import com.me.amqp.starter.queues.listeners.AMQPMessageListener;
import com.me.amqp.starter.rpc.clients.AMQPRPCClient;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPServiceAutoConfiguration {

        
        @Bean
	@ConditionalOnMissingBean
	public AMQPRPCMainServer aMQPRPCMainServer() throws Exception{
		return new AMQPRPCMainServer();
	}
        
        @Bean
	@ConditionalOnMissingBean
	public AMQPRPCClient aMQPRPCClient() {
		return new AMQPRPCClient();
	}
        
        @Bean
	@ConditionalOnMissingBean
	public AMQPMessageConfiguration aMQPMessageConfiguration() {
		return new AMQPMessageConfiguration();
	}
        
        @Bean
	@ConditionalOnMissingBean
	public AMQPMessageListener aMQPMessageListener() {
		return new AMQPMessageListener();
	}
        

}
