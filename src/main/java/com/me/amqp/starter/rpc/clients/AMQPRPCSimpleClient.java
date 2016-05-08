package com.me.amqp.starter.rpc.clients;

import ch.qos.logback.core.net.server.Client;
import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.rpc.servers.AMQPRPCMainServer;
import com.me.amqp.starter.utils.Utils;
//import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCSimpleClient {


    @Autowired
    AMQPServiceProperties aMQPServiceProperties;
    
    @Autowired
    private CachingConnectionFactory rabbitConnectionFactory;
    
    
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    @Autowired
    Utils utils;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCSimpleClient.class);

    @Bean
	public ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor() {
		return new ScheduledAnnotationBeanPostProcessor();
	}


//	@Bean
//	public ConnectionFactory connectionFactory() {
//		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
//		return connectionFactory;
//	}

//	@Bean
//	public RabbitAdmin admin() {
//		return new RabbitAdmin(connectionFactory());
//	}

	@Bean
	public RabbitTemplate rabbitTemplate(AMQPServiceProperties aMQPServiceProperties) {
		RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
		template.setExchange(exchangeName);
		template.setRoutingKey(bindingName);
		template.setReplyTimeout(2000);
		return template;
	}

	@Bean
	public DirectExchange exchange(AMQPServiceProperties aMQPServiceProperties) {
		return new DirectExchange(exchangeName, true, false);
	}

	@Bean
	Queue queue(AMQPServiceProperties aMQPServiceProperties) {
		return new Queue(queueName, true);
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange, AMQPServiceProperties aMQPServiceProperties) {
		return BindingBuilder.bind(queue).to(exchange).with(bindingName);
	}

	@Bean
	public AmqpProxyFactoryBean amqpProxyFactoryBean(AMQPServiceProperties aMQPServiceProperties) {
		AmqpProxyFactoryBean amqpProxyFactoryBean = new AmqpProxyFactoryBean();
		amqpProxyFactoryBean.setServiceInterface(CalculationService.class);
		amqpProxyFactoryBean.setAmqpTemplate(rabbitTemplate());
		return amqpProxyFactoryBean;
	}

	@Bean
	public CalculationService calculationService() throws Exception {
		return (CalculationService) amqpProxyFactoryBean().getObject();
	}
}
