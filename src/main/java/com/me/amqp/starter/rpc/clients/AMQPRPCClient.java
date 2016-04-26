
package com.me.amqp.starter.rpc.clients;

import java.util.Map;

public interface AMQPRPCClient {
    
    void sendRPCMessage(String operationHeader, String channelHeader, String payload) throws Exception;
    
    String sendAndReceiveRPCMessage(String payload) throws Exception;
    
    Map<String, Object> sendAndReceiveMapRPCMessage(Map<String, Object> payload) throws Exception;
}
