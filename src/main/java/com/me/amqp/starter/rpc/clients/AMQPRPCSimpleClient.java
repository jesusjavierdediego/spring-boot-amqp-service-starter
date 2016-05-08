package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerService;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerServiceAbstract;
import com.me.amqp.starter.utils.Utils;
//import com.rabbitmq.client.AMQP.BasicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCSimpleClient {


    @Autowired
    AMQPServiceProperties aMQPServiceProperties;
    
    @Autowired
    private CachingConnectionFactory rabbitConnectionFactory;
    
    @Autowired
    AMQPRPCDeliveryHandlerServiceAbstract aMQPRPCDeliveryHandlerServiceAbstract;
    
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

	@Bean
	public RabbitAdmin admin() {
		return new RabbitAdmin(rabbitConnectionFactory);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(AMQPServiceProperties aMQPServiceProperties) {
		RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
                template.setQueue(aMQPServiceProperties.getRpcqueuename());
		template.setExchange(aMQPServiceProperties.getRpcexchangename());
		template.setRoutingKey(aMQPServiceProperties.getRpcroutingKeyname());
		template.setReplyTimeout(Long.valueOf(aMQPServiceProperties.getReplytimeout()));
                template.setMessageConverter(jsonMessageConverter());
		return template;
	}
        
	@Bean
	public DirectExchange exchange(AMQPServiceProperties aMQPServiceProperties) {
		return new DirectExchange(aMQPServiceProperties.getRpcexchangename(), true, false);
	}

	@Bean
	Queue queue(AMQPServiceProperties aMQPServiceProperties) {
		return new Queue(aMQPServiceProperties.getRpcqueuename(), true);
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange, AMQPServiceProperties aMQPServiceProperties) {
		return BindingBuilder.bind(queue).to(exchange).with(aMQPServiceProperties.getBindingName());
	}

	@Bean
	public AmqpProxyFactoryBean amqpProxyFactoryBean(AMQPServiceProperties aMQPServiceProperties) {
		AmqpProxyFactoryBean amqpProxyFactoryBean = new AmqpProxyFactoryBean();
		amqpProxyFactoryBean.setServiceInterface(AMQPRPCDeliveryHandlerService.class);
		amqpProxyFactoryBean.setAmqpTemplate(rabbitTemplate(aMQPServiceProperties));
		return amqpProxyFactoryBean;
	}

//	@Bean
//	public CalculationService calculationService() throws Exception {
//		return (CalculationService) amqpProxyFactoryBean().getObject();
//	}
}
