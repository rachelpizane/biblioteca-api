package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Resumo dos dados do livro")
public record LivroResumoDTO (
        @Schema(description = "Identificador do livro", example = "7bbc8c0c-1d25-4c49-b61a-6ccdfac7b628")
        UUID id,
        @Schema(description = "Nome do livro", example = "A natureza da mordida")
        String nome
) {}