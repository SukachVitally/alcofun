package com.alco.searchapp.utils;

import com.alco.searchapp.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest extends BaseTest {
    @Test
    public void removeHtmlTags() {
        var newContent = StringUtils.removeAllHtmlTags(getResourceContent("raw_content/maudau.json"));

        Assertions.assertFalse(newContent.contains("</strong>"));
    }
}
