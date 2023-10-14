package com.alco.searchapp.provider;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.ProviderTypeEnum;
import java.time.Duration;
import java.util.List;
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
        var records = collector.collect(List.of(ProviderTypeEnum.ALCOMAG))
                .take(number)
                .timeout(Duration.ofSeconds(10))
                .collectList()
                .blockOptional()
                .orElseThrow();

        Assertions.assertEquals(number, records.size());
    }
}
