package com.alco.searchapp.controller.api;

import com.alco.searchapp.entity.ProductSearchEntity;
import com.alco.searchapp.service.ProductSearchService;
import com.alco.searchapp.service.SeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/api/seo/")
@RequiredArgsConstructor
public class SeoController {

    private static final String XML_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
    private static final String XML_END = "</urlset>";

    private final ProductSearchService productService;

    private final SeoService service;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public Mono<String> getSitemap() {
        var xmlBuffer = new StringBuffer();
        xmlBuffer.append(XML_START);
        xmlBuffer.append(createUrlBlock("/"));
        xmlBuffer.append(createUrlBlock("/products/"));

        return productService.getAllProducts().map(ProductSearchEntity::getId)
                .collectList()
                .flatMap(ids -> {
                    ids.forEach(id -> xmlBuffer.append(createUrlBlock(String.format("/products/%s", id))));
                    xmlBuffer.append(XML_END);
                    return Mono.just(xmlBuffer.toString());
                });
    }

    private String createUrlBlock(String path) {
        return String.format("<url><loc>%s%s</loc></url>", service.getBaseUrl(), path);
    }
}
