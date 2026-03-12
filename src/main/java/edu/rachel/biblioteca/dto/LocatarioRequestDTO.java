package edu.rachel.biblioteca.dto;

import edu.rachel.biblioteca.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados para cadastro de um locatário")
public record LocatarioRequestDTO(

        @Schema(description = "CPF do locatário", example = "10987654321")
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
        String cpf,

        @Schema(description = "Nome completo do locatário", example = "Maria Oliveira")
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Schema(description = "Sexo do locatário", example = "FEMININO")
        SexoEnum sexo,

        @Schema(description = "E-mail do locatário", example = "maria.oliveira@email.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(description = "Telefone do locatário (DD + número)", example = "11998765432")
        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "Telefone deve conter 11 dígitos")
        String telefone,

        @Schema(description = "Data de nascimento do locatário (YYYY-MM-DD)", example = "1990-05-15")
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser menor que a data atual")
        LocalDate dataNascimento
) {
}
