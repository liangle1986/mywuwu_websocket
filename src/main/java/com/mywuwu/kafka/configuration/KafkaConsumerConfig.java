package com.mywuwu.kafka.configuration;

import com.alibaba.druid.util.StringUtils;
import com.mywuwu.kafka.myListener.MyKafkaListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/28 11:28 Kafka消费者
 * @Description:
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private String commit;
    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private int interval;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String reset;
    @Value("${spring.kafka.consumer.auto-session-timeout}")
    private int timeout;
    @Value("${spring.kafka.consumer.key-deserializer}")
    private int kdeserializer;
    @Value("${spring.kafka.consumer.value-deserializer}")
    private int ydeserializer;


    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setPollTimeout(1500);
        return factory;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }


    public Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
//        "kafka集群IP1:9092,kafka集群IP2:9092"
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        //获取服务器Ip作为groupId 防止如果同组中存在多个监听对象不能结构到消息问题
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, getIPAddress());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, commit);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, interval);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, reset);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, timeout);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kdeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ydeserializer);
        return properties;
    }

    public String getIPAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            if (address != null && !StringUtils.isEmpty(address.getHostAddress())) {
                return address.getHostAddress();
            }
        } catch (UnknownHostException e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 自定义监听
     */
    @Bean
    public MyKafkaListener listener() {
        return new MyKafkaListener();
    }
}
