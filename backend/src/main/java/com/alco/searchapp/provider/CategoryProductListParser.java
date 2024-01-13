package com.alco.searchapp.provider;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class CategoryProductListParser implements Iterator<List<String>> {
    private final Function<Integer, String> linkFunction;

    private final Function<Document, List<String>> productListParser;

    private int currentIteration = 1;
    private List<String> links = List.of();

    private List<String> previousLinks = List.of();

    @Override
    public boolean hasNext() {
        try {
            Document doc = Jsoup.connect(linkFunction.apply(currentIteration)).get();
            links = productListParser.apply(doc);

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
}
