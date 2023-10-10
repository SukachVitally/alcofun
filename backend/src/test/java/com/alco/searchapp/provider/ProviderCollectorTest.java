package com.alco.searchapp.provider;

import com.alco.searchapp.BaseFunctionalTest;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProviderCollectorTest extends BaseFunctionalTest {
    @Autowired
    private ProviderCollector collector;

    @BeforeEach
    public void setup() {
        cleanDatabase();
    }

    @Test
    public void collect() {
        var number = 5;
        var records = collector.collect()
                .take(number)
                .timeout(Duration.ofSeconds(10))
                .collectList()
                .blockOptional()
                .orElseThrow();

        Assertions.assertEquals(number, records.size());
    }
}
