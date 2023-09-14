package com.alco.searchapp.constants;

import com.alco.searchapp.serializer.CharacteristicTypeEnumSerializer;
import com.alco.searchapp.serializer.ProviderTypeEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonSerialize(using = ProviderTypeEnumSerializer.class)
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
