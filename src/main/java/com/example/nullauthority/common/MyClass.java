package com.example.nullauthority.common;

import jakarta.annotation.Nullable;
import org.springframework.util.StringUtils;

public class MyClass {

    static void log(Object x) {
        System.out.println(x.toString());
    }

    static int checkModel(String s) {
        if (!StringUtils.hasText(s)) {
            return s.hashCode();
        }
        return 0;
    }

//    static void foo() {
//        log(null);
//    }

    static void bar() {
        log("test");
    }
}
