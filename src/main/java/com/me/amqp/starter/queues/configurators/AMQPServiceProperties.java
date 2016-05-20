package com.me.amqp.starter.queues.configurators;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(locations = "classpath:application.properties", ignoreUnknownFields = true, prefix = "amqp.service")
public class AMQPServiceProperties {

	private String rpcqueuename;
        private String rpcreplyqueuename;
        private String rpcqueueisDurable;
        private String rpcqueueexclusive;
        private String rpcqueueautodelete;
        private String rpcqueueautoack;
        
        private String queuename;
        private String queueisDurable;
        private String replytimeout;
        private String replyqueuename;
        private String replyqueueisDurable;
        private String replyexchangename;
        private String replyroutingKeyname;
        private String headersoperation;
        private String headerschannel;
        
        private String handlerclassname;
        private String rpchandlerclassname;
        private String handlerclassmethodname;
        private String rpchandlerclassmethodname;
        private String rpcmessagedefaultcontent;
        private String rpcroutingKeyname;
        private String rpcexchangename;
        
        private String operationheader;
        private String channelheader;
        
        private String RPCResponseTimeout;
        
       private String  rpcmessagedefaulterror;
       private String  bindingName ;
        


    public String getRpcqueuename() {
        return rpcqueuename;
    }

    public void setRpcqueuename(String rpcqueuename) {
        this.rpcqueuename = rpcqueuename;
    }

    public String getRpcreplyqueuename() {
        return rpcreplyqueuename;
    }

    public void setRpcreplyqueuename(String rpcreplyqueuename) {
        this.rpcreplyqueuename = rpcreplyqueuename;
    }

    public String getRpcqueueisDurable() {
        return rpcqueueisDurable;
    }

    public void setRpcqueueisDurable(String rpcqueueisDurable) {
        this.rpcqueueisDurable = rpcqueueisDurable;
    }

    public String getRpcqueueexclusive() {
        return rpcqueueexclusive;
    }

    public void setRpcqueueexclusive(String rpcqueueexclusive) {
        this.rpcqueueexclusive = rpcqueueexclusive;
    }

    public String getRpcqueueautodelete() {
        return rpcqueueautodelete;
    }

    public void setRpcqueueautodelete(String rpcqueueautodelete) {
        this.rpcqueueautodelete = rpcqueueautodelete;
    }

    public String getRpcqueueautoack() {
        return rpcqueueautoack;
    }

    public void setRpcqueueautoack(String rpcqueueautoack) {
        this.rpcqueueautoack = rpcqueueautoack;
    }

    public String getQueuename() {
        return queuename;
    }

    public void setQueuename(String queuename) {
        this.queuename = queuename;
    }

    public String getQueueisDurable() {
        return queueisDurable;
    }

    public void setQueueisDurable(String queueisDurable) {
        this.queueisDurable = queueisDurable;
    }

    public String getReplytimeout() {
        return replytimeout;
    }

    public void setReplytimeout(String replytimeout) {
        this.replytimeout = replytimeout;
    }

    public String getReplyqueuename() {
        return replyqueuename;
    }

    public void setReplyqueuename(String replyqueuename) {
        this.replyqueuename = replyqueuename;
    }

    public String getReplyqueueisDurable() {
        return replyqueueisDurable;
    }

    public void setReplyqueueisDurable(String replyqueueisDurable) {
        this.replyqueueisDurable = replyqueueisDurable;
    }

    public String getReplyexchangename() {
        return replyexchangename;
    }

    public void setReplyexchangename(String replyexchangename) {
        this.replyexchangename = replyexchangename;
    }

    public String getReplyroutingKeyname() {
        return replyroutingKeyname;
    }

    public void setReplyroutingKeyname(String replyroutingKeyname) {
        this.replyroutingKeyname = replyroutingKeyname;
    }

    public String getHeadersoperation() {
        return headersoperation;
    }

    public void setHeadersoperation(String headersoperation) {
        this.headersoperation = headersoperation;
    }

    public String getHeaderschannel() {
        return headerschannel;
    }

    public void setHeaderschannel(String headerschannel) {
        this.headerschannel = headerschannel;
    }

    public String getHandlerclassname() {
        return handlerclassname;
    }

    public void setHandlerclassname(String handlerclassname) {
        this.handlerclassname = handlerclassname;
    }

    public String getRpchandlerclassname() {
        return rpchandlerclassname;
    }

    public void setRpchandlerclassname(String rpchandlerclassname) {
        this.rpchandlerclassname = rpchandlerclassname;
    }

    public String getHandlerclassmethodname() {
        return handlerclassmethodname;
    }

    public void setHandlerclassmethodname(String handlerclassmethodname) {
        this.handlerclassmethodname = handlerclassmethodname;
    }

    public String getRpchandlerclassmethodname() {
        return rpchandlerclassmethodname;
    }

    public void setRpchandlerclassmethodname(String rpchandlerclassmethodname) {
        this.rpchandlerclassmethodname = rpchandlerclassmethodname;
    }

    public String getRpcmessagedefaultcontent() {
        return rpcmessagedefaultcontent;
    }

    public void setRpcmessagedefaultcontent(String rpcmessagedefaultcontent) {
        this.rpcmessagedefaultcontent = rpcmessagedefaultcontent;
    }

    public String getRpcroutingKeyname() {
        return rpcroutingKeyname;
    }

    public void setRpcroutingKeyname(String rpcroutingKeyname) {
        this.rpcroutingKeyname = rpcroutingKeyname;
    }

    public String getRpcexchangename() {
        return rpcexchangename;
    }

    public void setRpcexchangename(String rpcexchangename) {
        this.rpcexchangename = rpcexchangename;
    }
    
    public String getOperationheader() {
        return operationheader;
    }

    public void setOperationheader(String operationheader) {
        this.operationheader = operationheader;
    }

    public String getChannelheader() {
        return channelheader;
    }

    public void setChannelheader(String channelheader) {
        this.channelheader = channelheader;
    }

    public String getRPCResponseTimeout() {
        return RPCResponseTimeout;
    }

    public void setRPCResponseTimeout(String rPCResponseTimeout) {
        this.RPCResponseTimeout = rPCResponseTimeout;
    } 

    public String getRpcmessagedefaulterror() {
        return rpcmessagedefaulterror;
    }

    public void setRpcmessagedefaulterror(String rpcmessagedefaulterror) {
        this.rpcmessagedefaulterror = rpcmessagedefaulterror;
    }

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }
    
    
    
}