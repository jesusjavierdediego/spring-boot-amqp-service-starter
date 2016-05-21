
package com.me.amqp.starter.services;

public interface AMQPRPCDeliveryHandlerService {
    
    public byte[] handleMessage(byte[] message);
    
}
