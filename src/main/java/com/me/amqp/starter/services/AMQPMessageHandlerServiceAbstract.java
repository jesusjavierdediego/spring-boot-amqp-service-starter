package com.me.amqp.starter.services;


import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
    
    //Overriden method in abstract class
    private static final String HANDLER_METHOD_NAME = "handleIncomingMessage";

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPMessageHandlerServiceAbstract.class);
    

    public List<?> invokeHandler(Message message) {
        final CountDownLatch latch = new CountDownLatch(1);
        List<?> result = new ArrayList();
        try {
            Class<?> handlerClass = ClassUtils.forName(aMQPServiceProperties.getHandlerclassname(), getClass().getClassLoader());
            Method handleRPCIncomingMessage = ReflectionUtils.findMethod(handlerClass, HANDLER_METHOD_NAME);
            Object[] args = new Object[2];
            args[0] = message;
            result = (List<?>) ReflectionUtils.invokeMethod(handleRPCIncomingMessage, null, args);
            latch.await();
            return result;
        } catch (ClassNotFoundException cnfe) {
            LOGGER.error("[Queue message handling - Abstract] Error accessing to extended method handleRPCIncomingMessage in concrete service: {}", cnfe.getMessage());
        } catch (InterruptedException ie) {
            LOGGER.error("[Queue message handling - Abstract] Error handling process with CountDownLatch: {}", ie.getMessage());
        } catch (Exception e) {
            LOGGER.error("[Queue message handling - Abstract] Error handling process: {}", e.getMessage());
        }
        return result;
    }

    public abstract List<?> handleIncomingMessage(Message message);
}
