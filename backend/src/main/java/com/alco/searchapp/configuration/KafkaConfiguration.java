package com.alco.searchapp.configuration;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.ProviderTopicProperties;
import com.alco.searchapp.provider.dto.ProductPageDto;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfiguration {
    @Bean
    public ReactiveKafkaProducerTemplate<String, ProductPageDto> kafkaProducerTemplate(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }

    @Bean
    public Function<String, ReactiveKafkaConsumerTemplate<String, ProductPageDto>> consumerTemplateFactory(KafkaProperties properties) {
        return (String topic) -> {
            ReceiverOptions<String, ProductPageDto> options = ReceiverOptions.create(properties.buildConsumerProperties());
            options = options.subscription(Collections.singleton(topic));
            return new ReactiveKafkaConsumerTemplate<>(options);
        };
    }

    @Component
    @RequiredArgsConstructor
    public static class ProviderTopic {
        private final ProviderTopicProperties properties;

        public String getTopic(ProviderTypeEnum type) {
            return String.format("%s_%s", properties.getTopic(), type);
        }

        public NewTopic createNewTopic(ProviderTypeEnum type) {
            return new NewTopic(getTopic(type), properties.getPartitions(), properties.getReplicationFactor());
        }
    }

    @Bean
    public NewTopic alcomagTopic(ProviderTopic providerTopic) {
        return providerTopic.createNewTopic(ProviderTypeEnum.ALCOMAG);
    }

    @Bean
    public NewTopic rozetkaTopic(ProviderTopic providerTopic) {
        return providerTopic.createNewTopic(ProviderTypeEnum.ROZETKA);
    }

    @Bean
    public NewTopic maudauTopic(ProviderTopic providerTopic) {
        return providerTopic.createNewTopic(ProviderTypeEnum.MAUDAU);
    }

    @Bean
    public NewTopic okwineTopic(ProviderTopic providerTopic) {
        return providerTopic.createNewTopic(ProviderTypeEnum.OKWINE);
    }
}