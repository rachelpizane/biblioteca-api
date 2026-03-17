package edu.rachel.biblioteca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import edu.rachel.biblioteca.utils.EnumUtils;

public enum StatusEnum {
    EM_ANDAMENTO,
    FINALIZADO,
    CANCELADO;

    @JsonCreator
    public static StatusEnum fromString(String valor) {
        return EnumUtils.fromString(StatusEnum.class, valor);
    }
}
