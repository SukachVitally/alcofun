package com.alco.searchapp.constants;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CharacteristicTypeEnum {
    ALCOHOL_BY_VOLUME(1, "Alcohol by volume", "%", Float.class),
    COUNTRY(2, "Country", null, String.class),
    AGING(3, "Aging", "year", Float.class),
    VOLUME(4, "Volume", "ml", Integer.class),
    COLOR(5, "Color", null, String.class),
    CLASSIFICATION(6, "Classification", null, String.class),
    TASTE(7, "Taste", null, String.class),
    TEMPERATURE(8, "Temperature", null, Float.class),
    BRAND(9, "Brand", null, String.class);

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
