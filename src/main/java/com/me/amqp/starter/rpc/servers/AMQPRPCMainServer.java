package com.me.amqp.starter.rpc.servers;

import com.me.amqp.starter.queues.configurators.AMQPServiceProperties;
import com.me.amqp.starter.services.AMQPRPCDeliveryHandlerServiceAbstract;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Service
public class AMQPRPCMainServer{

    @Autowired
    AMQPRPCDeliveryHandlerServiceAbstract aMQPRPCDeliveryHandlerService;

    Connection connection;

    private final CountDownLatch latch = new CountDownLatch(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPRPCMainServer.class);

    @Autowired
    public AMQPRPCMainServer(ConnectionFactory connectionFactory, AMQPServiceProperties aMQPServiceProperties) {
        connection = connectionFactory.createConnection();
        try {
            Channel channel = connectionFactory.createConnection().createChannel(true);
            channel.queueDeclare(
                    aMQPServiceProperties.getRpcqueuename(),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueisDurable()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueexclusive()),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueautodelete()),
                    null
            );
            channel.exchangeDeclare(aMQPServiceProperties.getRpcexchangename(), "topic");

            DefaultConsumer consumer = new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(
                        String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                            .correlationId(properties.getCorrelationId())
                            .build();

                    byte[] processedResult = aMQPRPCDeliveryHandlerService.invokeHandler(body);
                    LOGGER.info("Response: {}", new String(processedResult));
                    try {
                        channel.basicPublish(aMQPServiceProperties.getRpcexchangename(), aMQPServiceProperties.getRpcroutingKeyname(), replyProps, processedResult);
                        //channel.basicAck(envelope.getDeliveryTag(), false);
                        
                    } catch (IOException ioe) {
                        LOGGER.error("[RPC - Server handleDelivery()] Error handling process: {}", ioe.getMessage());
                    }

                    latch.countDown();
                }
            };

            channel.basicQos(1);
            channel.basicConsume(
                    aMQPServiceProperties.getRpcqueuename(),
                    Boolean.valueOf(aMQPServiceProperties.getRpcqueueautoack()),
                    consumer
            );
            LOGGER.info("[RPC - Server Constructor] Awaiting RPC requests in channel");
        } catch (IOException ioe) {
            //TODO
            LOGGER.error("[RPC - Server Constructor] Error handling process: {}", ioe.getMessage());
        }

        /*
         AMQP.Queue.DeclareOk queueDeclare(java.lang.String queue,
         boolean durable,
         boolean exclusive,
         boolean autoDelete,
         java.util.Map<java.lang.String,java.lang.Object> arguments)
         throws java.io.IOException
         Declare a queue
         Parameters:
         queue - the name of the queue
         durable - true if we are declaring a durable queue (the queue will survive a server restart)
         exclusive - true if we are declaring an exclusive queue (restricted to this connection)
         autoDelete - true if we are declaring an autodelete queue (server will delete it when no longer in use)
         arguments - other properties (construction arguments) for the queue
         Returns:RPC_QUEUE_AUTODELETE
         a declaration-confirm method to indicate the queue was successfully declared
         Throws:
         java.io.IOException - if an error is encountered
         */
        /*
         java.lang.String basicConsume(java.lang.String queue,
         boolean autoAck,
         Consumer callback)
         throws java.io.IOException
         Start a non-nolocal, non-exclusive consumer, with a server-generated consumerTag.
         Parameters:
         queue - the name of the queue
         autoAck - true if the server should consider messages acknowledged once delivered; false if the server should expect explicit acknowledgements
         callback - an interface to the consumer object
         Returns:
         the consumerTag generated by the server
         Throws:
         java.io.IOException - if an error is encountered
         */
    }

    public void close() throws Exception {
        try {
            connection.close();
        } catch (NullPointerException npe) {
            LOGGER.error("[RPC - Server close()] No connection to be closed: {}", npe.getMessage());
        }
    }

}
