package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Resumo dos dados do autor")
public record AutorResumoDTO(
        @Schema(description = "Identificador do autor", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Nome do autor", example = "Carla Madeira")
        String nome
) {}