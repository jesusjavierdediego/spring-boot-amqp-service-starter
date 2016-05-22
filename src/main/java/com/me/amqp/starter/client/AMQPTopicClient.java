package com.me.amqp.starter.client;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AMQPTopicClient {
    
    @Autowired
    @Qualifier("topicRabbitTemplate")
    RabbitTemplate TopicRabbitTemplate;

    public void sendMessageToTopic(String message){
        TopicRabbitTemplate.convertAndSend(message);
    }
    
    
}
