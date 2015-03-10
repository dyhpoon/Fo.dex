package com.dyhpoon.fodex.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by darrenpoon on 10/3/15.
 */
public class StringUtils {
    public static List<String> tokenize(String s) {
        String[] tags = s.toString().split("\\s+");
        List<String> filteredTags = new LinkedList<>(Arrays.asList(tags));
        filteredTags.removeAll(Arrays.asList("", null));
        return filteredTags;
    }
}
