package com.me.amqp.starter.rpc.clients;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import org.springframework.stereotype.Component;
import sun.applet.Main;

@Component
public class AMQPRPCClient {

    public static byte[] rpc(final Channel ch, String queue, byte[] req)
        throws Exception
    {
        final byte[][] results = new byte[1][];
        final CountDownLatch latch = new CountDownLatch(1);

        DefaultConsumer c = new DefaultConsumer(ch) {
            @Override
            public void handleDelivery(
                    String consumerTag,
                    Envelope envelope,
                    AMQP.BasicProperties properties,
                    byte[] body) throws IOException
            {
                results[0] = body;
                latch.countDown();
            }
        };
        String ctag = ch.basicConsume(Main.DIRECT_QUEUE, true, c);
        System.out.printf("ctag: %s", ctag);
        System.out.println();

        AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.
            builder().replyTo(Main.DIRECT_QUEUE).build();
        ch.basicPublish("", queue, props, req);
        latch.await();
        ch.basicCancel(ctag);

        return results[0];
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Client!");

        ConnectionFactory f = new ConnectionFactory();
        f.setHost(Main.HOST);
        Connection conn = f.newConnection();
        final Channel ch = conn.createChannel();

        byte[] resp = rpc(ch, Main.SERVER_QUEUE, "Hello server!".getBytes());
        System.out.println(new String(resp));

        conn.close();
    }
}