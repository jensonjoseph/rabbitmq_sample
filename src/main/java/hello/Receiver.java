package hello;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

//    public CountDownLatch getLatch() {
//        return latch;
//    }


}
