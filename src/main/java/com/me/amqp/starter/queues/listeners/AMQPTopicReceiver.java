
package com.me.amqp.starter.queues.listeners;

import java.util.concurrent.CountDownLatch;


public class AMQPTopicReceiver {
    
    private CountDownLatch latch = new CountDownLatch(1);

	public void receiveMessage(String message) {
		System.out.println("Received <" + message + ">");
		latch.countDown();
	}

	public CountDownLatch getLatch() {
		return latch;
	}
    
}
