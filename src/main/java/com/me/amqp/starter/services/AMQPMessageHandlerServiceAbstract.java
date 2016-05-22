package com.me.amqp.starter.services;


import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@Component
public abstract class AMQPMessageHandlerServiceAbstract {

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;

    private static final String HANDLERMETHOD = "handleIncomingMessage";
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPMessageHandlerServiceAbstract.class);
    

    public List<?> invokeHandler(Message message) {
        List<?> result = new ArrayList();
        try {
            Class<?> handlerClass = ClassUtils.forName(aMQPServiceProperties.getTopicHandlerClassName(), getClass().getClassLoader());
            Object handlerObject = handlerClass.newInstance();
            Method handleRPCIncomingMessage = ReflectionUtils.findMethod(handlerClass, HANDLERMETHOD, new Class[]{byte[].class});
            Object[] args = new Object[1]; 
            args[0] = message;
            result = (List<?>) ReflectionUtils.invokeMethod(handleRPCIncomingMessage, handlerObject, args);
            return result;
        } catch (ClassNotFoundException cnfe) {
            LOGGER.error("[Queue message handling - Abstract] Error accessing to extended method handleRPCIncomingMessage in concrete service: {}", cnfe.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("[Queue message handling - Abstract] Error handling process: {}", e.getMessage());
        }
        return result;
    }

    public abstract List<?> handleIncomingMessage(Message message);
}