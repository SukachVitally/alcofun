package com.alco.searchapp.provider;

import com.alco.searchapp.exception.ProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlLinkParser {
    private final WebClient client;

    public Mono<Document> parse(String url) {
        return client.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(new ProviderException(String.format("Cannot get page %s", url)))
                )
                .bodyToMono(String.class)
                .map(Jsoup::parse);
    }
}
