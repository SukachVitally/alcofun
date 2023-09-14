package com.alco.searchapp.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public String removeAllHtmlTags(String content) {
        return content.replaceAll("\\<.*?\\>", "");
    }
}
