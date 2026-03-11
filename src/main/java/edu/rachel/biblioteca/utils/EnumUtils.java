package edu.rachel.biblioteca.utils;

import edu.rachel.biblioteca.exception.EnumInvalidoException;

public class EnumUtils {
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String valor) {
        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(valor)) {
                return constant;
            }
        }
        throw new EnumInvalidoException(enumClass.getSimpleName(), valor);
    }

    private EnumUtils(){}
}
