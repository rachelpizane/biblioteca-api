package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dados paginados contendo o conteúdo principal e informações de paginação")
public record PageResponseDTO<T>(
        @Schema(description = "Conteúdo da página")
        List<T> conteudo,
        @Schema(description = "Número da página atual", example = "0")
        int pagina,
        @Schema(description = "Tamanho da página", example = "5")
        int tamanho,
        @Schema(description = "Total de elementos", example = "100")
        long totalElementos,
        @Schema(description = "Total de páginas", example = "10")
        int totalPaginas
) {}
