package com.alco.searchapp.provider;

import com.alco.searchapp.BaseTest;
import com.alco.searchapp.configuration.HttpClientConfiguration;
import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;

public class AbstractProviderTest extends BaseTest {

    protected WebClient getClient() {
        return new HttpClientConfiguration().client();
    }

    protected ObjectMapper objectMapper() {
        return new ObjectMapperConfiguration().objectMapper();
    }

    protected LinkParser linkParser() {
        return new LinkParser(getClient(), objectMapper());
    }
}
