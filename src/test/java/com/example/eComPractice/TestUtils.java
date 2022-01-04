package com.example.eComPractice;

import java.lang.reflect.Field;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) throws NoSuchFieldException, IllegalAccessException {
        boolean wasPrivate = false;

        Field f = target.getClass().getDeclaredField(fieldName);
        if (!f.isAccessible()) {
            f.setAccessible(true);
            wasPrivate = true;
        }
        f.set(target, toInject);
        if(wasPrivate) {
            f.setAccessible(false);
        }
    }
}
