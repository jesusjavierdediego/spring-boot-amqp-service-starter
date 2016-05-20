
package com.me.amqp.starter.services;


public interface AMQPRPCDeliveryHandlerService {
    
    public byte[] invokeHandler(byte[] message);
    
    public byte[] handleRPCIncomingMessage(byte[] message);
    
}
