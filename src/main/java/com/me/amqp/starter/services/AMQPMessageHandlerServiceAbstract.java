package com.me.amqp.starter.services;

import com.rabbitmq.client.Channel;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@Component
public abstract class AMQPMessageHandlerServiceAbstract {
    
    //Full qualified class name extending abstract class
    @Value("${amqp.service.starter.handler.class.name}")
    private String DECLARED_HANDLER_CLASS_NAME;
    
    //Overriden method in abstract class
    private static final String HANDLER_METHOD_NAME = "handleRPCIncomingMessage";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPMessageHandlerServiceAbstract.class);


    public List<?> invokeHandler(Message message, Channel channel) {
        try {
            Class<?> handlerClass = ClassUtils.forName(DECLARED_HANDLER_CLASS_NAME, getClass().getClassLoader());
            Method handleRPCIncomingMessage = ReflectionUtils.findMethod(handlerClass, HANDLER_METHOD_NAME);
            Object[] args = new Object[2]; 
            args[0] = message;
            args[1] = channel;
            List<?> result = (List<?>)ReflectionUtils.invokeMethod(handleRPCIncomingMessage, null, args);
            return result;
        } catch (ClassNotFoundException cnfe) {
            LOGGER.error("[Queue message handling - Abstract] Error accessing to extended method handleRPCIncomingMessage in concrete service: {}", cnfe.getMessage());
            return new ArrayList();
        }
    }

    public abstract List<?> handleIncomingMessage(Message message, Channel channel);
}
