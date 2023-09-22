package com.alco.searchapp.configuration;

import co.elastic.clients.transport.ElasticsearchTransport;
import com.alco.searchapp.properites.ElasticsearchProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.erhlc.ReactiveRestClients;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfiguration extends ReactiveElasticsearchConfiguration {
    private final ElasticsearchProperty property;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(property.getUrl())
                .build();
    }
}
