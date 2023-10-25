package com.alco.searchapp.configuration;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.constants.TopicConstants;
import com.alco.searchapp.exception.TopicNotFoundException;
import com.alco.searchapp.properites.ProviderTopicProperties;
import com.alco.searchapp.provider.dto.ProductPageDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
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

    @Component
    @RequiredArgsConstructor
    public static class ProviderTopic {
        private final ProviderTopicProperties properties;

        private final Map<ProviderTypeEnum, String> map = Map.of(
                ProviderTypeEnum.ALCOMAG, TopicConstants.PRODUCT_ALCOMAG,
                ProviderTypeEnum.ROZETKA, TopicConstants.PRODUCT_ROZETKA,
                ProviderTypeEnum.MAUDAU, TopicConstants.PRODUCT_MAUDAU,
                ProviderTypeEnum.OKWINE, TopicConstants.PRODUCT_OKWINE
        );

        public String getTopic(ProviderTypeEnum type) {
            if (!map.containsKey(type)) {
                throw new TopicNotFoundException(
                        String.format("Topic for provider type %s is not found", type.getName()));
            }
            return map.get(type);
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
