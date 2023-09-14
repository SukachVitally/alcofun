package com.alco.searchapp.utils;

import com.alco.searchapp.dto.CharacteristicDto;
import java.util.Comparator;

public class CharacteristicComparator implements Comparator<CharacteristicDto> {
    @Override
    public int compare(CharacteristicDto item1, CharacteristicDto item2) {
        Object value1 = item1.getValue();
        Object value2 = item2.getValue();

        if (value1 instanceof Integer && value2 instanceof Integer) {
            return ((Integer) value1).compareTo((Integer) value2);
        } else if (value1 instanceof Float && value2 instanceof Float) {
            return Float.compare((Float) value1, (Float) value2);
        } else if (value1 instanceof String && value2 instanceof String) {
            return ((String) value1).compareTo((String) value2);
        } else {
            return 0;
        }
    }
}
