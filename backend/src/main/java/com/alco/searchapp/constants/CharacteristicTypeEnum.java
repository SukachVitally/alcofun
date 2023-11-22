package com.alco.searchapp.constants;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// todo: Add internationalization
@RequiredArgsConstructor
@Getter
public enum CharacteristicTypeEnum {
    ALCOHOL_BY_VOLUME(1, "Міцність", "%", Float.class),
    COUNTRY(2, "Страна", null, String.class),
    AGING(3, "Витримка", "років", Float.class),
    VOLUME(4, "Об'єм", "ml", Integer.class),
    COLOR(5, "Колір", null, String.class),
    CLASSIFICATION(6, "Классифікація", null, String.class),
    TASTE(7, "Смак", null, String.class),
    TEMPERATURE(8, "Температура", null, Float.class),
    BRAND(9, "Бренд", null, String.class);

    private final int id;

    private final String name;

    private final String unitOfMeasurement;

    private final Class<?> type;

    public static CharacteristicTypeEnum findById(Integer id) {
        return Arrays.stream(values())
                .filter(instance -> instance.getId() == id)
                .findAny()
                .orElseThrow();
    }
}
