package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Schema(description = "Dados sobre um livro")
public record LivroResponseDTO(
        @Schema(description = "Identificador único do livro", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Nome do Livro", example = "A natureza da mordida")
        String nome,

        @Schema(description = "ISBN do livro", example = "9783161484100")
        String isbn,

        @Schema(description = "Data de publicação do livro (YYYY-MM-DD)", example = "2022-11-21")
        LocalDate dataPublicacao,

        @Schema(
                description = "Lista de autores",
                example = "[{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"nome\": \"Carla Madeira\"}]"
        )
        List<AutorResumoDTO> autores
) {
}

