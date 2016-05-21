package com.me.amqp.starter.queues.configurators;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(locations = "classpath:application.properties", ignoreUnknownFields = true, prefix = "amqp.service")
public class AMQPServiceProperties {

    private String rpcQueueName;
    private String rpcReplyQueueName;
    private String rpcQueueIsDurable;
    private String rpcQueueIsexclusive;
    private String rpcQueueIsautodelete;
    private String rpcQueueIsautoack;
    private String rpcRoutingKeyname;
    private String rpcExchangeName;
    private String rpcBindingName;
    
    private String topicQueueName;
    private String topicQueueisDurable;
    private String topicQueueIsExclusive;
    private String topicQueueIsAutodelete;
    private String topicExchangeName;
    private String topicBindingName;
    
    private String replytimeout;
    private String replyQueueName;
    
    private String handlerClassName;
    private String rpcHandlerClassName;
    private String handlerClassMethodName;
    private String rpcHandlerclassmethodName;
    private String rpcMessagedefaultContent;
    
    
    private String operationHeader;
    private String channelHeader;
    private String RPCResponseTimeout;
    private String rpcMessageDefaultError;
    private String defaultEncoding;
    
    

    public String getRpcQueueName() {
        return rpcQueueName;
    }

    public String getRpcReplyQueueName() {
        return rpcReplyQueueName;
    }

    public String getRpcQueueIsDurable() {
        return rpcQueueIsDurable;
    }

    public String getRpcQueueIsexclusive() {
        return rpcQueueIsexclusive;
    }

    public String getRpcQueueIsautodelete() {
        return rpcQueueIsautodelete;
    }

    public String getRpcQueueIsautoack() {
        return rpcQueueIsautoack;
    }

    public String getRpcRoutingKeyname() {
        return rpcRoutingKeyname;
    }

    public String getRpcExchangeName() {
        return rpcExchangeName;
    }

    public String getRpcBindingName() {
        return rpcBindingName;
    }

    public String getTopicQueueName() {
        return topicQueueName;
    }

    public String getTopicQueueisDurable() {
        return topicQueueisDurable;
    }

    public String getTopicQueueIsExclusive() {
        return topicQueueIsExclusive;
    }

    public String getTopicQueueIsAutodelete() {
        return topicQueueIsAutodelete;
    }

    public String getTopicExchangeName() {
        return topicExchangeName;
    }

    public String getTopicBindingName() {
        return topicBindingName;
    }

    public String getReplytimeout() {
        return replytimeout;
    }

    public String getReplyQueueName() {
        return replyQueueName;
    }

    public String getHandlerClassName() {
        return handlerClassName;
    }

    public String getRpcHandlerClassName() {
        return rpcHandlerClassName;
    }

    public String getHandlerClassMethodName() {
        return handlerClassMethodName;
    }

    public String getRpcHandlerclassmethodName() {
        return rpcHandlerclassmethodName;
    }

    public String getRpcMessagedefaultContent() {
        return rpcMessagedefaultContent;
    }

    public String getOperationHeader() {
        return operationHeader;
    }

    public String getChannelHeader() {
        return channelHeader;
    }

    public String getRPCResponseTimeout() {
        return RPCResponseTimeout;
    }

    public String getRpcMessageDefaultError() {
        return rpcMessageDefaultError;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }  
}
