package com.me.amqp.starter.rpc.servers;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerService;
import java.util.logging.Level;
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
import org.springframework.util.ClassUtils;

@Service
public class AMQPRPServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPServer.class);

    @Autowired
    CachingConnectionFactory rabbitConnectionFactory;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    public AMQPRPServer(CachingConnectionFactory rabbitConnectionFactory) {
        this.rabbitConnectionFactory = rabbitConnectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange exchange(AMQPServiceProperties aMQPServiceProperties) {
        return new DirectExchange(aMQPServiceProperties.getRpcexchangename(), true, false);
    }

    @Bean
    Queue queue(AMQPServiceProperties aMQPServiceProperties) {
        return new Queue(
                aMQPServiceProperties.getRpcreplyqueuename(),
                Boolean.valueOf(aMQPServiceProperties.getRpcqueueisDurable()),
                Boolean.valueOf(aMQPServiceProperties.getRpcqueueexclusive()),
                Boolean.valueOf(aMQPServiceProperties.getRpcqueueautodelete())
        );
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange, AMQPServiceProperties aMQPServiceProperties) {
        return BindingBuilder.bind(queue).to(exchange).with(aMQPServiceProperties.getBindingName());

    }

    @Bean
    public AMQPRPCDeliveryHandlerService service(AMQPServiceProperties aMQPServiceProperties) throws InstantiationException {
        AMQPRPCDeliveryHandlerService handlerObject = null;
        try {
            Class<?> handlerClass = ClassUtils.forName(aMQPServiceProperties.getRpchandlerclassname(), getClass().getClassLoader());
            handlerObject = (AMQPRPCDeliveryHandlerService) handlerClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException e) {
            LOGGER.error("ERROR RPC SERVER: {}", e.getMessage());
        }

        return handlerObject;
    }

    @Bean
    public AmqpInvokerServiceExporter listener(AMQPServiceProperties aMQPServiceProperties) throws InstantiationException {
        //try{
        //Class<?> handlerClass = ClassUtils.forName(aMQPServiceProperties.getRpchandlerclassname(), getClass().getClassLoader());
        AmqpInvokerServiceExporter serviceExporter = new AmqpInvokerServiceExporter();
        serviceExporter.setService(AMQPRPCDeliveryHandlerService.class);
        serviceExporter.setService(service(aMQPServiceProperties));
        serviceExporter.setAmqpTemplate(rabbitTemplate);
        serviceExporter.setMessageConverter(jsonMessageConverter());
        return serviceExporter;
//        }catch(ClassNotFoundException ce){
//            LOGGER.debug("[AMQP - RPC- Server] The class for the handler interface has not been found.");
//            throw new AMQPException("[AMQP - RPC- Server] The class for the handler interface has not been found.");
//        }
    }

    @Bean
    SimpleMessageListenerContainer container(AMQPServiceProperties aMQPServiceProperties) throws InstantiationException {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(this.rabbitConnectionFactory);
        container.setQueueNames(aMQPServiceProperties.getRpcqueuename());
        container.setMessageListener(listener(aMQPServiceProperties));
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }
}
