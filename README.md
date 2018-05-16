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
  @Bean
    DirectExchange exchange() {
        return new DirectExchange(topicExchangeName);
    }
    
   @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }
    
   @Bean
    DirectExchange exchange() {
        return new DirectExchange(topicExchangeName);
    }
    
  ### A message listener container
  @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    
  @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
  ### A component to send some message to test the listener  
  
  package hello;

//import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    //private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        //this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(Application.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
        //receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}

