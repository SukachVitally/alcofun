package com.alco.searchapp.provider;

import com.alco.searchapp.exception.LinkParserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkParser {
    private final WebClient client;

    private final ObjectMapper mapper;

    public Mono<String> get(String url) {
        return client.get()
                .uri(url)
                .header(HttpHeaders.HOST, getUrlHost(url))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(new LinkParserException(String.format("Cannot get page %s", url)))
                )
                .bodyToMono(String.class)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorResume(throwable -> Mono.error(new LinkParserException(throwable.getMessage())));
    }

    public Mono<Document> parseHtml(String url) {
        return get(url)
                .onErrorReturn("")
                .map(Jsoup::parse);
    }

    public <T> Mono<T> parseJson(String url, Class<T> type) {
        return get(url)
                .onErrorReturn("{}")
                .flatMap(str -> {
                    try {
                        return Mono.justOrEmpty(mapper.readValue(str, type));
                    } catch (IOException e) {
                        return Mono.empty();
                    }
                });
    }

    private static String getUrlHost(String url) {
        // Parse the url and extract the host
        // This is a simplified example, robust parsing should be done in actual scenario based on your needs
        return url.replace("http://", "").replace("https://", "").split("/")[0];
    }
}
