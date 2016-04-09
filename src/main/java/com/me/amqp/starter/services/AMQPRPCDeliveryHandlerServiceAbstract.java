package com.me.amqp.starter.services;

import com.rabbitmq.client.Channel;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@Component
public abstract class AMQPRPCDeliveryHandlerServiceAbstract {

    //Full qualified class name extending abstract class
    @Value("${amqp.service.starter.rpc.handler.class.name}")
    private String DECLARED_HANDLER_CLASS_NAME;
    
    @Value("${amqp.service.starter.rpc.message.default.content}")
    private String RPC_MESSAGE_DEFAULT_CONTENT;

    //Overriden method in abstract class
    private static final String HANDLER_METHOD_NAME = "handleRPCIncomingMessage";

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCDeliveryHandlerServiceAbstract.class);
    
    public byte[] invokeHandler(byte[] message, Channel channel) {
        byte[] result = RPC_MESSAGE_DEFAULT_CONTENT.getBytes();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            Class<?> handlerClass = ClassUtils.forName(DECLARED_HANDLER_CLASS_NAME, getClass().getClassLoader());
            Method handleRPCIncomingMessage = ReflectionUtils.findMethod(handlerClass, HANDLER_METHOD_NAME);
            Object[] args = new Object[2]; 
            args[0] = message;
            args[1] = channel;
            result = (byte[])ReflectionUtils.invokeMethod(handleRPCIncomingMessage, null, args);
            latch.await();
        } catch (ClassNotFoundException cnfe) {
            LOGGER.error("[RPC message handling - Abstract] Error handling process: {}", cnfe.getMessage());
        } catch (InterruptedException ie) {
            LOGGER.error("[RPC message handling - Abstract] Error handling process with CountDownLatch: {}", ie.getMessage());
        } catch (Exception e) {
            LOGGER.error("[RPC message handling - Abstract] Error handling process: {}", e.getMessage());
        }
        
        return result;
    }

    public abstract byte[] handleRPCIncomingMessage(byte[] message, Channel channel);

}
