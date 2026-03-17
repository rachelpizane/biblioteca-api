package edu.rachel.biblioteca.dto;

import edu.rachel.biblioteca.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para alteração de status de um aluguel")
public record StatusRequestDTO(
        @Schema(
                description = "Novo status do aluguel",
                example = "FINALIZADO",
                allowableValues = {"FINALIZADO", "CANCELADO"}
        )
        @NotNull(message = "O status do aluguel é obrigatório")
        StatusEnum status
) {
}
