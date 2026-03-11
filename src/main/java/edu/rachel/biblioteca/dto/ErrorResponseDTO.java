package edu.rachel.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dados de resposta para erros da API")
public record ErrorResponseDTO(
        @Schema(description = "Código de status HTTP", example = "500")
        int codigo,

        @Schema(
                description = "Lista de mensagens que detallha os erros ocorridos",
                example = "[\"Erro interno no servidor\"]"
        )
        List<String> mensagens) {
}
