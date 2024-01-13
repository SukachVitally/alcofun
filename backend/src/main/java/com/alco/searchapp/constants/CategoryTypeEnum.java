package com.alco.searchapp.constants;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// todo: Add internationalization
@RequiredArgsConstructor
@Getter
public enum CategoryTypeEnum {
    VINE(1, "Вино"),
    VODKA(2, "Водка"),
    CHAMPAGNE(3, "Вино ігристе"),
    COGNAC(4, "Коньяк"),
    WHISKEY(5, "Віскі"),
    LIQUEUR(6, "Лікер"),
    TEQUILA(7, "Текіла"),
    BRANDY(8, "Бренді"),
    BEER(9, "Пиво"),
    VERMOUTH(10, "Вермут"),
    RUM(11, "Ром"),
    GIN(12, "Джин"),
    ABSENT(13, "Абсент"),
    SAKE(14, "Саке"),
    CHACHA(15, "Чача"),
    GRAPPA(16, "Граппа"),
    ARMAGNAC(17, "Арманьяк"),
    CIDER(18, "Сидр"),
    CALVADOS(19, "Кальвадос");

    private final int id;

    private final String name;

    public static CategoryTypeEnum findById(Integer id) {
        return Arrays.stream(values())
                .filter(instance -> instance.getId() == id)
                .findAny()
                .orElseThrow();
    }
}
