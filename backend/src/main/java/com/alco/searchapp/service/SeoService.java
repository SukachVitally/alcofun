package com.alco.searchapp.service;

import com.alco.searchapp.properites.SeoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeoService {
    private final SeoProperties properties;

    public String getBaseUrl() {
        return properties.getBaseUrl();
    }
}
