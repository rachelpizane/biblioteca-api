package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados para cadastro de um livro")
public record LivroRequestDTO(
        @Schema(description = "Nome do Livro", example = "A natureza da mordida")
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Schema(description = "ISBN do livro", example = "9783161484100")
        @NotBlank(message = "ISBN é obrigatório")
        @Pattern(regexp = "\\d{13}", message = "ISBN deve conter 13 dígitos")
        String isbn,

        @Schema(description = "Data de publicação do livro (YYYY-MM-DD)", example = "2022-11-21")
        @NotNull(message = "Data de publicação é obrigatória")
        LocalDate dataPublicacao,

        @Schema(
                description = "Lista de autores (apenas IDs)",
                example = "[\"123e4567-e89b-12d3-a456-426614174000\"]"
        )
        @NotEmpty(message = "A lista de autores deve conter pelo menos um autor")
        List<UUID> autoresIds
) {
}
