package com.alco.searchapp;

import java.nio.file.Files;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

abstract public class BaseTest {
    @SneakyThrows
    protected String getResourceContent(String path) {
        var resource = new ClassPathResource(path);
        return new String(Files.readAllBytes(resource.getFile().toPath()));
    }
}
