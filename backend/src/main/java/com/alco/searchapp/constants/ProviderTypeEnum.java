package com.alco.searchapp.constants;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProviderTypeEnum {
    ALCOMAG(1, "Alcomag"),
    ROZETKA(2, "Rozetka"),
    OKWINE(3, "Okwine"),
    MAUDAU(4, "Maudau");

    private final int id;

    private final String name;

    public static ProviderTypeEnum findById(Integer id) {
        return Arrays.stream(values())
                .filter(instance -> instance.getId() == id)
                .findAny()
                .orElseThrow();
    }
}
