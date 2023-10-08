package com.alco.searchapp.constants;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryTypeEnum {
    VINE(1, "Vine"),
    VODKA(2, "Vodka"),
    CHAMPAGNE(3, "Champagne"),
    COGNAC(4, "Cognac"),
    WHISKEY(5, "Whiskey"),
    LIQUEUR(6, "Liqueur"),
    TEQUILA(7, "Tequila"),
    BRANDY(8, "Brandy"),
    BEER(9, "Beer"),
    VERMOUTH(10, "Vermouth"),
    RUM(11, "Rum"),
    GIN(12, "Gin"),
    ABSENT(13, "Absent"),
    SAKE(14, "Sake"),
    CHACHA(15, "Chaha"),
    GRAPPA(16, "Grappa"),
    ARMAGNAC(17, "Armagnac");

    private final int id;

    private final String name;

    public static CategoryTypeEnum findById(Integer id) {
        return Arrays.stream(values())
                .filter(instance -> instance.getId() == id)
                .findAny()
                .orElseThrow();
    }
}
