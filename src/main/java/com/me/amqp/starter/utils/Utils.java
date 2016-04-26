
package com.me.amqp.starter.utils;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Utils {
    
    @Autowired
    AMQPServiceProperties aMQPServiceProperties;
    
    
    public Entry getOperationHeaderValueFromMessage(Map<String, Object> headers){
        return headers.entrySet().parallelStream().filter(e -> e.getKey().equalsIgnoreCase(aMQPServiceProperties.getChannelheader())).findFirst().get();
    }
    
    public Entry getChannelHeaderValueFromMessage(Map<String, Object> headers){
        return headers.entrySet().parallelStream().filter(e -> e.getKey().equalsIgnoreCase(aMQPServiceProperties.getOperationheader())).findFirst().get();
    }
    
    public Map<String, Object>  getMessagePropertiesFromList(String opHeaderValue, String channelHeaderValue){
        Map<String, Object> props = new HashMap<>();
        props.put(aMQPServiceProperties.getHeadersoperation(), opHeaderValue);
        props.put(aMQPServiceProperties.getChannelheader(), channelHeaderValue);
        return props;
    }
    
}
