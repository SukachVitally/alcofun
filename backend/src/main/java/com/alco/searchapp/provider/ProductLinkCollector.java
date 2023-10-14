package com.alco.searchapp.provider;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// todo: Make this collector not blocking
@RequiredArgsConstructor
public class ProductLinkCollector implements Iterable<List<String>> {
    private final Function<Integer, String> createLink;

    private final String selector;

    @Override
    public Iterator<List<String>> iterator() {
        return new Iterator<>() {
            private int currentIteration = 1;
            private List<String> previousLinks = List.of();
            private List<String> links;

            @Override
            public boolean hasNext() {
                try {
                    Document doc = Jsoup.connect(createLink.apply(currentIteration)).get();
                    links = doc.select(selector).stream()
                            .map(element -> element.attr("href"))
                            .toList();

                    return !links.isEmpty() && links.stream().noneMatch(previousLinks::contains);
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public List<String> next() {
                currentIteration++;
                previousLinks = links;

                return links;
            }
        };
    }
}