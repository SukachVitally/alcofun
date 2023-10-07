package com.alco.searchapp.configuration;

import com.alco.searchapp.properites.ElasticsearchProperties;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfiguration extends ReactiveElasticsearchConfiguration {
    private final ElasticsearchProperties properties;

    @NotNull
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(properties.getUrl())
                .build();
    }
}
