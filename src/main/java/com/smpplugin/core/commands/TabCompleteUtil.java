package com.smpplugin.core.commands;

import java.util.ArrayList;
import java.util.List;

final class TabCompleteUtil {

    private TabCompleteUtil() {
    }

    static List<String> filter(Iterable<String> options, String prefix) {
        List<String> result = new ArrayList<>();
        String lower = prefix.toLowerCase();
        for (String option : options) {
            if (option.startsWith(lower)) {
                result.add(option);
            }
        }
        return result;
    }
}
