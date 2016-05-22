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
    private String rpcExchangeName;
    private String rpcBindingName;
    private String rpcReplyTimeout;
    private String rpcHandlerClassName;
    private String rpcMessagedefaultContent;
    private String rpcMessageDefaultError;
    
    private String topicQueueName;
    private String topicQueueisDurable;
    private String topicQueueIsExclusive;
    private String topicQueueIsAutodelete;
    private String topicExchangeName;
    private String topicBindingName;
    private String topicHandlerClassName;
    
    private String operationHeader;
    private String channelHeader;
    private String defaultEncoding;
    
    
    

    public String getRpcQueueName() {
        return rpcQueueName;
    }

    public void setRpcQueueName(String rpcQueueName) {
        this.rpcQueueName = rpcQueueName;
    }

    public String getRpcReplyQueueName() {
        return rpcReplyQueueName;
    }

    public void setRpcReplyQueueName(String rpcReplyQueueName) {
        this.rpcReplyQueueName = rpcReplyQueueName;
    }

    public String getRpcQueueIsDurable() {
        return rpcQueueIsDurable;
    }

    public void setRpcQueueIsDurable(String rpcQueueIsDurable) {
        this.rpcQueueIsDurable = rpcQueueIsDurable;
    }

    public String getRpcQueueIsexclusive() {
        return rpcQueueIsexclusive;
    }

    public void setRpcQueueIsexclusive(String rpcQueueIsexclusive) {
        this.rpcQueueIsexclusive = rpcQueueIsexclusive;
    }

    public String getRpcQueueIsautodelete() {
        return rpcQueueIsautodelete;
    }

    public void setRpcQueueIsautodelete(String rpcQueueIsautodelete) {
        this.rpcQueueIsautodelete = rpcQueueIsautodelete;
    }

    public String getRpcQueueIsautoack() {
        return rpcQueueIsautoack;
    }

    public void setRpcQueueIsautoack(String rpcQueueIsautoack) {
        this.rpcQueueIsautoack = rpcQueueIsautoack;
    }

    public String getRpcExchangeName() {
        return rpcExchangeName;
    }

    public void setRpcExchangeName(String rpcExchangeName) {
        this.rpcExchangeName = rpcExchangeName;
    }

    public String getRpcBindingName() {
        return rpcBindingName;
    }

    public void setRpcBindingName(String rpcBindingName) {
        this.rpcBindingName = rpcBindingName;
    }

    public String getRpcReplyTimeout() {
        return rpcReplyTimeout;
    }

    public void setRpcReplyTimeout(String rpcReplytimeout) {
        this.rpcReplyTimeout = rpcReplytimeout;
    }

    public String getRpcHandlerClassName() {
        return rpcHandlerClassName;
    }

    public void setRpcHandlerClassName(String rpcHandlerClassName) {
        this.rpcHandlerClassName = rpcHandlerClassName;
    }


    public String getRpcMessagedefaultContent() {
        return rpcMessagedefaultContent;
    }

    public void setRpcMessagedefaultContent(String rpcMessagedefaultContent) {
        this.rpcMessagedefaultContent = rpcMessagedefaultContent;
    }

    public String getRpcMessageDefaultError() {
        return rpcMessageDefaultError;
    }

    public void setRpcMessageDefaultError(String rpcMessageDefaultError) {
        this.rpcMessageDefaultError = rpcMessageDefaultError;
    }

    public String getTopicQueueName() {
        return topicQueueName;
    }

    public void setTopicQueueName(String topicQueueName) {
        this.topicQueueName = topicQueueName;
    }

    public String getTopicQueueisDurable() {
        return topicQueueisDurable;
    }

    public void setTopicQueueisDurable(String topicQueueisDurable) {
        this.topicQueueisDurable = topicQueueisDurable;
    }

    public String getTopicQueueIsExclusive() {
        return topicQueueIsExclusive;
    }

    public void setTopicQueueIsExclusive(String topicQueueIsExclusive) {
        this.topicQueueIsExclusive = topicQueueIsExclusive;
    }

    public String getTopicQueueIsAutodelete() {
        return topicQueueIsAutodelete;
    }

    public void setTopicQueueIsAutodelete(String topicQueueIsAutodelete) {
        this.topicQueueIsAutodelete = topicQueueIsAutodelete;
    }

    public String getTopicExchangeName() {
        return topicExchangeName;
    }

    public void setTopicExchangeName(String topicExchangeName) {
        this.topicExchangeName = topicExchangeName;
    }

    public String getTopicBindingName() {
        return topicBindingName;
    }

    public void setTopicBindingName(String topicBindingName) {
        this.topicBindingName = topicBindingName;
    }

    public String getTopicHandlerClassName() {
        return topicHandlerClassName;
    }

    public void setTopicHandlerClassName(String topicHandlerClassName) {
        this.topicHandlerClassName = topicHandlerClassName;
    }

    public String getOperationHeader() {
        return operationHeader;
    }

    public void setOperationHeader(String operationHeader) {
        this.operationHeader = operationHeader;
    }

    public String getChannelHeader() {
        return channelHeader;
    }

    public void setChannelHeader(String channelHeader) {
        this.channelHeader = channelHeader;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }
    
    

    
}
