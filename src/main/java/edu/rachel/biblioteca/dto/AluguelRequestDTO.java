package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados para cadastro de um aluguel")
public record AluguelRequestDTO(
        @Schema(description = "Data de retirada do aluguel", example = "2024-06-01")
        @NotNull(message = "Data de retirada é obrigatória")
        @PastOrPresent(message = "Data de retirada deve ser hoje ou anterior")
        LocalDate dataRetirada,

        @Schema(description = "Data de devolução do aluguel", example = "2024-06-03")
        LocalDate dataDevolucao,

        @Schema(description = "ID do locatário", example = "be8bd6b1-3569-4485-98eb-9cb43473f915")
        @NotNull(message = "Locatário é obrigatório")
        UUID locatarioId,

        @Schema(description = "Lista de IDs dos livros alugados", example = "[\"7bbc8c0c-1d25-4c49-b61a-6ccdfac7b628\"]")
        @NotEmpty(message = "A lista de livros deve conter pelo menos um livro")
        List<UUID> livrosIds
) {
        @AssertTrue(message = "Data de devolução deve ser posterior à data de retirada")
        public boolean isDataDevolucaoValida() {
                return dataDevolucao == null || dataRetirada == null || dataDevolucao.isAfter(dataRetirada);
        }
}