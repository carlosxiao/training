package com.cc.springbootredissubscriber.config;

import com.cc.springbootredissubscriber.Receiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.CountDownLatch;

/**
 * Created by CarlosXiao on 2018/7/1.
 */

@Configuration
public class SubscriberConfig {

    @Value("${redis.msg.topic}")
    private String redisTopic;

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter,new PatternTopic(redisTopic));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(Receiver receiver){
        return new MessageListenerAdapter(receiver,"receiveMessage");
    }

    @Bean
    public Receiver receiver(CountDownLatch latch){
        return new Receiver(latch);
    }

    @Bean
    public CountDownLatch latch(){
        return new CountDownLatch(1);
    }
}
