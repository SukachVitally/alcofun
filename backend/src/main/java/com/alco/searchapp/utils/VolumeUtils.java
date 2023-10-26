package com.alco.searchapp.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class VolumeUtils {
    public Integer parse(String rawValue) {
        rawValue = rawValue.replace(",", ".");
        try {
            var coefficient = rawValue.contains("мл") ? 1 : 1000;

            return (int) (Float.parseFloat(rawValue.replaceAll("[^\\d.]", "")) * coefficient);
        } catch (NumberFormatException e) {
            log.error(String.format("Invalid volume raw value '%s'", rawValue), e);
            return null;
        }
    }
}
