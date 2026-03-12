package edu.rachel.biblioteca.dto;

import edu.rachel.biblioteca.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Dados para cadastro de um autor")
public record AutorRequestDTO(

        @Schema(description = "CPF do autor", example = "48323788723")
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
        String cpf,

        @Schema(description = "Nome completo do autor", example = "Carlos Silva")
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Schema(description = "Sexo do autor", example = "MASCULINO")
        SexoEnum sexo,

        @Schema(description = "Ano de nascimento do autor", example = "1986")
        @NotNull(message = "Ano de nascimento é obrigatório")
        @Positive(message = "Ano de nascimento deve ser positivo")
        int anoNascimento
) {
}
