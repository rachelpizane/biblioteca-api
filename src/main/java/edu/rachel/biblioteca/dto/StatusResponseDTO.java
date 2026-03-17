package edu.rachel.biblioteca.dto;

import edu.rachel.biblioteca.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados sobre o status do aluguel após atualização")
public record StatusResponseDTO(
        @Schema(description = "Identificador único do aluguel", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID idAluguel,
        @Schema(description = "Status do aluguel", example = "FINALIZADO")
        StatusEnum status
) {
}
