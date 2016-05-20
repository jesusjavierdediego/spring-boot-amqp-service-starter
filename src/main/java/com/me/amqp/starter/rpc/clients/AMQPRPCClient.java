package com.me.amqp.starter.rpc.clients;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerService;
import com.me.amqp.starter.utils.Utils;
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
import org.springframework.stereotype.Service;

@Service
public class AMQPRPCClient {

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;

    @Autowired
    Utils utils;

    private CachingConnectionFactory rabbitConnectionFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCClient.class);

    @Autowired
    public AMQPRPCClient(CachingConnectionFactory rabbitConnectionFactory) {
        this.rabbitConnectionFactory = rabbitConnectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
	public RabbitAdmin admin() {
		return new RabbitAdmin(rabbitConnectionFactory);
	}

    @Bean
    public RabbitTemplate rabbitRPCTemplate(AMQPServiceProperties aMQPServiceProperties) {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setQueue(aMQPServiceProperties.getRpcqueuename());
        template.setExchange(aMQPServiceProperties.getRpcexchangename());
        template.setRoutingKey(aMQPServiceProperties.getBindingName());
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
        return new Queue(
                aMQPServiceProperties.getRpcqueuename(),
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
    public AmqpProxyFactoryBean amqpProxyFactoryBean(AMQPServiceProperties aMQPServiceProperties) {
//        try{
//            Class<?> handlerClass = ClassUtils.forName(aMQPServiceProperties.getRpchandlerclassname(), getClass().getClassLoader());
        AmqpProxyFactoryBean amqpProxyFactoryBean = new AmqpProxyFactoryBean();
        amqpProxyFactoryBean.setServiceInterface(AMQPRPCDeliveryHandlerService.class);
        amqpProxyFactoryBean.setAmqpTemplate(rabbitRPCTemplate(aMQPServiceProperties));
        return amqpProxyFactoryBean;
//        }catch(ClassNotFoundException ce){
//            LOGGER.debug("[AMQP - RPC- Client] The class for the handler interface to be invoked has not been found.");
//            throw new AMQPException("[AMQP - RPC- Client] The class for the handler interface to be invoked has not been found.");
//        }
    }


    @Bean
    public AMQPRPCDeliveryHandlerService aMQPRPCDeliveryHandlerService(AMQPServiceProperties aMQPServiceProperties) throws Exception {
        return (AMQPRPCDeliveryHandlerService) amqpProxyFactoryBean(aMQPServiceProperties).getObject();
    }

}
