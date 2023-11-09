package com.alco.searchapp.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AlcoholByVolumeUtil {
    public Float parse(String rawValue) {
        var value = rawValue
                .replace(",", ".")
                .replace("%", "")
                .replace(" ", "")
                .replaceAll("^.*?-+", "");

        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
