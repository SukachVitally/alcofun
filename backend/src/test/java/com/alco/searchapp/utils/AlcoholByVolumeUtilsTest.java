package com.alco.searchapp.utils;

import com.alco.searchapp.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AlcoholByVolumeUtilsTest extends BaseTest {
    @Test
    public void parseSimpleValueWithoutPoint() {
        var value  = AlcoholByVolumeUtil.parse("12%");

        Assertions.assertEquals(12f, value);
    }

    @Test
    public void parseSimpleValueWithPoint() {
        var value  = AlcoholByVolumeUtil.parse("12.5%");

        Assertions.assertEquals(12.5f, value);
    }

    @Test
    public void parseSimpleValueWithInvalidPoint() {
        var value  = AlcoholByVolumeUtil.parse("12,5%");

        Assertions.assertEquals(12.5f, value);
    }

    @Test
    public void parseSimpleValueWithRange() {
        var value  = AlcoholByVolumeUtil.parse("10,5-12,5%");

        Assertions.assertEquals(12.5f, value);
    }

    @Test
    public void parseSimpleValueWithRangeAndSpaces() {
        var value  = AlcoholByVolumeUtil.parse("10,5 - 12,5 %");

        Assertions.assertEquals(12.5f, value);
    }

    @Test
    public void parseSimpleValueWithInvalidValue() {
        var value  = AlcoholByVolumeUtil.parse("invalid%");

        Assertions.assertNull(value);
    }
}
