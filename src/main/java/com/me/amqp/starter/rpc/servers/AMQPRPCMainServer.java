package com.me.amqp.starter.rpc.servers;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerServiceAbstract;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;

@Service
public class AMQPRPCMainServer {

    private final CountDownLatch latch = new CountDownLatch(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCMainServer.class);

    @Autowired
    CachingConnectionFactory rabbitConnectionFactory;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AMQPRPCDeliveryHandlerServiceAbstract aMQPRPCDeliveryHandlerServiceAbstract;

    @Autowired
    public AMQPRPCMainServer(CachingConnectionFactory rabbitConnectionFactory) {
        this.rabbitConnectionFactory = rabbitConnectionFactory;
    }
    
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
//        return connectionFactory;
//    }

    @Bean
    public DirectExchange exchange(AMQPServiceProperties aMQPServiceProperties) {
        return new DirectExchange(aMQPServiceProperties.getRpcexchangename(), true, false);
    }

    @Bean
    Queue queue(AMQPServiceProperties aMQPServiceProperties) {
        return new Queue(
                aMQPServiceProperties.getReplyqueuename(), 
                Boolean.valueOf(aMQPServiceProperties.getRpcqueueisDurable()
                ));
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange, AMQPServiceProperties aMQPServiceProperties) {
        return BindingBuilder.bind(queue).to(exchange).with(aMQPServiceProperties.getBindingName());
    }

//    @Bean
//    public CalculationService service(AMQPServiceProperties aMQPServiceProperties) {
//        return new CalculationServiceImpl();
//    }
    @Bean
    public AmqpInvokerServiceExporter listener(AMQPServiceProperties aMQPServiceProperties) {
        AmqpInvokerServiceExporter serviceExporter = new AmqpInvokerServiceExporter();
        serviceExporter.setService(AMQPRPCDeliveryHandlerServiceAbstract.class);
        serviceExporter.setService(aMQPRPCDeliveryHandlerServiceAbstract);
        serviceExporter.setAmqpTemplate(rabbitTemplate);
        serviceExporter.setMessageConverter(jsonMessageConverter());
        return serviceExporter;
    }

    @Bean
    SimpleMessageListenerContainer container(AMQPServiceProperties aMQPServiceProperties) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(this.rabbitConnectionFactory);
        container.setQueueNames(aMQPServiceProperties.getRpcqueuename());
        container.setMessageListener(listener(aMQPServiceProperties));
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }
}
