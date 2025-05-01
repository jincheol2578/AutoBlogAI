package com.autoblog.autoblog.util;

import java.util.Optional;
import java.util.function.Consumer;

public class CommonUtills {

    public static void updateFieldIfNotEmpty(String value, Consumer<String> setter) {
        Optional.ofNullable(value)
            .filter(v -> !v.isEmpty())
            .ifPresent(v -> setter.accept(v)); // 값이 있을 경우만 setter를 호출
    }
    
}
