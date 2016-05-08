
package com.me.amqp.starter.services;


public interface AMQPRPCDeliveryHandlerService {
    
    public byte[] invokeHandler(byte[] message);
    
    public abstract byte[] handleRPCIncomingMessage(byte[] message);
    
}
