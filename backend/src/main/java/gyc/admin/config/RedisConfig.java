package gyc.admin.config;

import gyc.admin.events.ConsumerEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Pub/Sub only — no consumer groups or offsets. A message published while
 * this service is offline is not redelivered on restart. Acceptable for
 * this skeleton; the documented upgrade path is Redis Streams.
 */
@Configuration
public class RedisConfig {

    public static final String CONSUMER_EVENTS_CHANNEL = "consumer.events";

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            ConsumerEventListener consumerEventListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(consumerEventListener, new ChannelTopic(CONSUMER_EVENTS_CHANNEL));
        return container;
    }
}
