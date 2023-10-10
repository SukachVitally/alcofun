package com.alco.searchapp.service;

import com.alco.searchapp.properites.QueueProperties;
import com.alco.searchapp.provider.dto.ProductPageDto;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveKafkaProducerTemplate<String, ProductPageDto> producerTemplate;

    private final Function<String, ReactiveKafkaConsumerTemplate<String, ProductPageDto>> consumerFactory;

    private final QueueProperties properties;

    public Mono<RecordMetadata> sendProductPage(ProductPageDto pageDto) {
        return send(properties.getProductTopic(), pageDto);
    }

    public Flux<ProductPageDto> receiveProductPages() {
        return receive(properties.getProductTopic());
    }

    private Mono<RecordMetadata> send(String topic, ProductPageDto message) {
        return producerTemplate
                .send(topic, message)
                .map(SenderResult::recordMetadata);

    }

    private Flux<ProductPageDto> receive(String topic) {
        return consumerFactory.apply(topic).receiveAutoAck()
                .doOnNext(consumerRecord -> log.info("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset())
                )
                .map(ConsumerRecord::value);
    }
}
