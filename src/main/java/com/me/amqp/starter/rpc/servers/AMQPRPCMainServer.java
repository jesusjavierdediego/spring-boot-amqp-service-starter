package com.me.amqp.starter.rpc.servers;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerServiceAbstract;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.amqp.rabbit.connection.Connection;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.context.annotation.Bean;

@Service
public class AMQPRPCMainServer {

            
    private Connection connection;

    private final CountDownLatch latch = new CountDownLatch(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCMainServer.class);
    
    @Autowired
    private CachingConnectionFactory rabbitConnectionFactory;



	@Autowired
	RabbitTemplate rabbitTemplate;
        
        @Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		return connectionFactory;
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
	public CalculationService service(AMQPServiceProperties aMQPServiceProperties) {
		return new CalculationServiceImpl();
	}

	@Bean
	public AmqpInvokerServiceExporter listener(AMQPServiceProperties aMQPServiceProperties) {
		AmqpInvokerServiceExporter serviceExporter = new AmqpInvokerServiceExporter();
		serviceExporter.setService(CalculationService.class);
		serviceExporter.setService(service(aMQPServiceProperties));
		serviceExporter.setAmqpTemplate(rabbitTemplate);
		return serviceExporter;
	}

	@Bean
	SimpleMessageListenerContainer container(AMQPServiceProperties aMQPServiceProperties) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(queueName);
		container.setMessageListener(listener(aMQPServiceProperties));
		return container;
	}
}
