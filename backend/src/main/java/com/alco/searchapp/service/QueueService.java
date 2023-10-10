package com.alco.searchapp.service;

import com.alco.searchapp.provider.dto.ProductPageDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.scheduler.enable", havingValue = "true")
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveKafkaProducerTemplate<String, ProductPageDto> producerTemplate;

    private final ReactiveKafkaConsumerTemplate<String, ProductPageDto> consumerTemplate;

    // Move product topic name to config
    public Mono<RecordMetadata> sendProductPage(ProductPageDto pageDto) {
        return send("products", pageDto);
    }

    public Flux<ProductPageDto> receiveProductPages() {
        return receive("products").map(record -> (ProductPageDto) record);
    }

    private Mono<RecordMetadata> send(String topic, ProductPageDto message) {
        String key = UUID.randomUUID().toString();
        return producerTemplate
                .send(topic, 1, key, message)
                .map(SenderResult::recordMetadata);

    }

    // todo: Configure topic in service!!!
    private Flux<Object> receive(String topic) {
        return consumerTemplate.receiveAutoAck()
                .doOnNext(consumerRecord -> log.info("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset())
                ).map(ConsumerRecord::value);
    }
}
