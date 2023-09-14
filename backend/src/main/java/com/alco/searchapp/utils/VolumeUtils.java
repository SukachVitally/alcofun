package com.alco.searchapp.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class VolumeUtils {
    public Integer parse(String rawValue) {
        rawValue = rawValue.replace(",", ".");
        try {
            if (rawValue.contains(" мл")) {
                return Integer.parseInt(rawValue.replace(" мл", ""));
            }

            if (rawValue.contains(" літр")) {
                return (int) (Float.parseFloat(rawValue.replace(" літр", "")) * 1000);
            }

            if (rawValue.contains(" л")) {
                return (int) (Float.parseFloat(rawValue.replace(" л", "")) * 1000);
            }

            float value = Float.parseFloat(rawValue);
            if (value == (int) value) {
                return (int) value;
            }
            return (int) (value * 1000);
        } catch (NumberFormatException e) {
            log.error(String.format("Invalid volume raw value '%s'", rawValue), e);
            return null;
        }
    }
}
