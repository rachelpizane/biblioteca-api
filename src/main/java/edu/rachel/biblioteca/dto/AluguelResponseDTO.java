package edu.rachel.biblioteca.dto;

import edu.rachel.biblioteca.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados sobre um aluguel")
public record AluguelResponseDTO(
        @Schema(description = "Identificador único do aluguel", example = "58cdbd12-a124-43ba-89d1-935b28605223")
        UUID id,

        @Schema(description = "Data de retirada do aluguel", example = "2024-06-01")
        LocalDate dataRetirada,

        @Schema(description = "Data de devolução do aluguel", example = "2024-06-03")
        LocalDate dataDevolucao,

        @Schema(description = "Status do aluguel", example = "EM_ANDAMENTO")
        StatusEnum status,

        @Schema(
                description =  "Dados do locatário",
                example = "{\"id\": \"be8bd6b1-3569-4485-98eb-9cb43473f915\", \"nome\": \"Maria Oliveira\"}"
        )
        LocatarioResumoDTO locatario,

        @Schema(
                description = "Lista de livros alugados",
                example = "[{\"id\": \"7bbc8c0c-1d25-4c49-b61a-6ccdfac7b628\", \"nome\": \"A natureza da mordida\"}]"
        )
        List<LivroResumoDTO> livros
) {}
