
package com.me.amqp.starter.services;

import java.util.List;
import org.springframework.amqp.core.Message;


public interface AMQPDeliveryHandlerService {
    
    public List<?> invokeHandler(Message message);
    
    public List<?> handleIncomingMessage(Message message);
    
}
