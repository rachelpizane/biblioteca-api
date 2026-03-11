package edu.rachel.biblioteca.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import edu.rachel.biblioteca.utils.EnumUtils;

public enum SexoEnum {
    MASCULINO,
    FEMININO,
    OUTROS;

    @JsonCreator
    public static SexoEnum fromString(String valor) {
        return EnumUtils.fromString(SexoEnum.class, valor);
    }
}
