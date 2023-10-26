package com.alco.searchapp.utils;

import com.alco.searchapp.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VolumeUtilsTest extends BaseTest {
    @Test
    public void parseSampleInLiters() {
        var value  = VolumeUtils.parse("1.5 літра");

        Assertions.assertEquals(1500, value);
    }

    @Test
    public void parseSampleInLiters2() {
        var value  = VolumeUtils.parse("1 літр");

        Assertions.assertEquals(1000, value);
    }

    @Test
    public void parseSampleInLiters3() {
        var value  = VolumeUtils.parse("1 л");

        Assertions.assertEquals(1000, value);
    }

    @Test
    public void parseSampleInLiters4() {
        var value  = VolumeUtils.parse("1літр");

        Assertions.assertEquals(1000, value);
    }

    @Test
    public void parseSampleInLiters5() {
        var value  = VolumeUtils.parse("1100мл");

        Assertions.assertEquals(1100, value);
    }
}
