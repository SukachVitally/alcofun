package com.alco.searchapp.configuration;

import com.alco.searchapp.provider.dto.ProductPageDto;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduler.enable", havingValue = "true")
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfiguration {
    @Bean
    public ReactiveKafkaProducerTemplate<String, ProductPageDto> kafkaProducerTemplate(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, ProductPageDto> kafkaConsumerTemplate(KafkaProperties properties) {
        ReceiverOptions<String, ProductPageDto> options = ReceiverOptions.create(properties.buildConsumerProperties());
        options = options.subscription(Collections.singleton("products"));
        return new ReactiveKafkaConsumerTemplate<>(options);
    }
}