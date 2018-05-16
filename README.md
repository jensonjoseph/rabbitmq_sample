# rabbitmq_sample
Streps for implementing queuing
## 1. brew install rabbitmq

## 2. Create a RabbitMQ receiver
  The receiver may be a simple POJO that defines a method for receiving messages. 
  When you register to receive messages, you can name it to anything you want. 
  
 for e.g. 
 package hello;

import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}



## Alternatively, you may implement ChannelAwareMessageListener in receiver POJO. 
package hello;

import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Receiver implements ChannelAwareMessageListener {

//    private CountDownLatch latch = new CountDownLatch(1);

//    public void receiveMessage(String message) {
//        System.out.println("Received <" + message + ">");
//        //latch.countDown();
//    }

    /**
     * Callback for processing a received Rabbit message.
     * <p>Implementors are supposed to process the given Message,
     * typically sending reply messages through the given Session.
     *
     * @param message the received AMQP message (never <code>null</code>)
     * @param channel the underlying Rabbit Channel (never <code>null</code>)
     * @throws Exception Any.
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        String str = new String(message.getBody(), StandardCharsets.UTF_8);
        System.out.println("***** Received <" + str + "> *****");
        System.out.println("***** Channel : " + channel.toString() + " *****");
    }
}

## Register the listener and send the message 
We need to configure the following:
  ### Declare the exchange, queue and the binding between them
  
  ### A message listener container
  
  ### A component to send some message to test the listener  
