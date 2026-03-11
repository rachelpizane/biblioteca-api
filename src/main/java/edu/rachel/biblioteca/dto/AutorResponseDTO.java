package edu.rachel.biblioteca.dto;

import edu.rachel.biblioteca.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados sobre um autor")
public record AutorResponseDTO(
        @Schema(description = "Identificador único do autor", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "CPF do autor", example = "48323788723")
        String cpf,

        @Schema(description = "Nome completo do autor", example = "Carlos Silva")
        String nome,

        @Schema(description = "Sexo do autor", example = "MASCULINO")
        SexoEnum sexo,

        @Schema(description = "Ano de nascimento do autor", example = "1986")
        int anoNascimento
) {
}
