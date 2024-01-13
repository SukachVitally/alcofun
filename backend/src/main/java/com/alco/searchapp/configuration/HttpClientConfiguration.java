package com.alco.searchapp.configuration;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.function.Consumer;

@Configuration
public class HttpClientConfiguration {
    @Bean
    public WebClient client() {
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> spec.sslContext(createTrustAllSslContext()));

        return WebClient.builder()
                .filter(logRequest())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeaders(this::defaultHeaders)
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

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method());
            System.out.println("Request URI: " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> System.out.println(name + ":" + value)));
            return Mono.just(clientRequest);
        });
    }

    private void defaultHeaders(HttpHeaders headers) {
        headers.set(HttpHeaders.USER_AGENT, "curl/7.64.1");
    }
}
