package com.fdeight.utils;

import com.yworks.util.annotation.Obfuscation;

/**
 * Класс, предназначеный для проверки работы блокирования обфрускации для использования рефлексии.
 * Заметим, что yGuard достаточно успешно справляется (т.е. обфрусцирует не только имя класса, но и изменяет
 * соответствующую строковую константу) с ситуацией, когда полное имя класса находится внутри
 * исходного кода в явном виде (например, "com.fdeight.utils.Utils").
 * Но если имя класса формируется динамически, или же поступает из внешних источников, то для такого класса следует
 * использовать аннотацию {@link Obfuscation} со значениями exclude = true (по умолчанию) и applyToMembers = false.
 */
@Obfuscation(applyToMembers = false)
public class DataByName {

    public static String getValueByReflection(final String className) {
        try {
            final Class aClass = Class.forName(className);
            @SuppressWarnings("deprecation")
            final Object object = aClass.newInstance();
            if (object instanceof DataByName) {
                return String.format("className: [%s], object: [%s], value: [%s]",
                        className, object, ((DataByName) object).getValue());
            } else {
                return String.format("className: [%s], object: [%s]", className, object);
            }
        } catch (final Exception e) {
            return String.valueOf(e);
        }
    }

    private String getValue() {
        return "Value";
    }

    public static String getComString() {
        return "com";
    }
}
