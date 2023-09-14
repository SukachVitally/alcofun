package com.alco.searchapp.configuration;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class HttpClientConfiguration {
    @Bean
    public WebClient client() {
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> spec.sslContext(createTrustAllSslContext()));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(codecs -> codecs
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024)) // todo: add to web client property
                .build();
    }

    private static SslContext createTrustAllSslContext() {
        try {
            return SslContextBuilder.forClient()
                    .sslProvider(SslProvider.JDK)
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating SSL context", e);
        }
    }
}
