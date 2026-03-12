package edu.rachel.biblioteca.dto;

import edu.rachel.biblioteca.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Dados sobre um locatário")
public record LocatarioResponseDTO(
        @Schema(description = "Identificador único do locatário", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "CPF do locatário", example = "10987654321")
        String cpf,

        @Schema(description = "Nome completo do locatário", example = "Maria Oliveira")
        String nome,

        @Schema(description = "Sexo do locatário", example = "FEMININO")
        SexoEnum sexo,

        @Schema(description = "E-mail do locatário", example = "maria.oliveira@email.com")
        String email,

        @Schema(description = "Telefone do locatário (DD + numero)", example = "11998765432")
        String telefone,

        @Schema(description = "Data de nascimento do locatário (YYYY-MM-DD)", example = "1990-05-15")
        LocalDate dataNascimento
) {
}
