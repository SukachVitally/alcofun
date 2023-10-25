package com.alco.searchapp.service;

import com.alco.searchapp.configuration.KafkaConfiguration;
import com.alco.searchapp.provider.dto.ProductPageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveKafkaProducerTemplate<String, ProductPageDto> producerTemplate;

    private final KafkaConfiguration.ProviderTopic providerTopic;

    public Mono<RecordMetadata> sendProductPage(ProductPageDto message) {
        return producerTemplate
                .send(providerTopic.getTopic(message.getProvider()), message)
                .map(SenderResult::recordMetadata);
    }
}
