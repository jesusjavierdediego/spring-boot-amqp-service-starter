package com.me.amqp.starter.services;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@Component
public abstract class AMQPRPCDeliveryHandlerServiceAbstract {

    @Autowired
    AMQPServiceProperties aMQPServiceProperties;
    

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCDeliveryHandlerServiceAbstract.class);

    
    public byte[] invokeHandler(byte[] message) {
        byte[] result = aMQPServiceProperties.getRpcmessagedefaultcontent().getBytes();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            Class<?> handlerClass = ClassUtils.forName(aMQPServiceProperties.getRpchandlerclassname(), getClass().getClassLoader());
            Method handleRPCIncomingMessage = ReflectionUtils.findMethod(handlerClass, aMQPServiceProperties.getRpchandlerclassmethodname());
            Object[] args = new Object[2]; 
            args[0] = message;
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

    public abstract byte[] handleRPCIncomingMessage(byte[] message);

}
