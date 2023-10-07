package com.alco.searchapp.service;

import com.alco.searchapp.provider.dto.ProductPageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveKafkaProducerTemplate<String, String> producerTemplate;

    private final ObjectMapper mapper;

    // Move product topic name to config
    public Mono<RecordMetadata> sendProductPage(ProductPageDto pageDto) {
        return send("products", pageDto);
    }

    private Mono<RecordMetadata> send(String topic, Object message) {
        String key = UUID.randomUUID().toString();
        try {
            String value = mapper.writeValueAsString(message);
            return producerTemplate
                    .send(topic, 1, key, value)
                    .map(SenderResult::recordMetadata);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
