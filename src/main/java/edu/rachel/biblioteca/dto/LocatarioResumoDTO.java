package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Resumo dos dados do locatário")
public record LocatarioResumoDTO (
        @Schema(description = "Identificador do locatário", example = "be8bd6b1-3569-4485-98eb-9cb43473f915")
        UUID id,
        @Schema(description = "Nome do locatário", example = "Maria Oliveira")
        String nome
) {}