package com.alco.searchapp.provider;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// todo: Make this collector not blocking
@RequiredArgsConstructor
public class ProductLinkCollector implements Iterable<List<String>> {
    private final String url;

    private final String selector;

    private final String pageGetParameter;

    private final String pageGetParameterSuffix;

    @Override
    public Iterator<List<String>> iterator() {
        return new Iterator<>() {
            private int currentIteration = 1;
            private int size;
            private List<String> links;

            @Override
            public boolean hasNext() {
                try {
                    Document doc = Jsoup.connect(createLink(currentIteration)).get();
                    links = doc.select(selector).stream()
                            .map(element -> element.attr("href"))
                            .toList();

                    if (size < links.size()) {
                        size = links.size();
                    }

                    return links.size() >= size;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public List<String> next() {
                currentIteration++;

                return links;
            }

            private String createLink(int page) {
                return String.format("%s?%s=%s%s", url, pageGetParameter, page, pageGetParameterSuffix);
            }
        };
    }
}