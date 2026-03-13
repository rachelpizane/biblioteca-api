package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
        name = "Aluguéis",
        description = "Operações para gerenciamento de aluguéis"
)
@RequestMapping(value = "/alugueis")
public interface AluguelApi {
    @Operation(
            summary = "Cadastrar um novo aluguel",
            description = "Recebe os dados de um aluguel e retorna o aluguel cadastrado"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Aluguel cadastrado com sucesso",
            content = @Content(
                    schema = @Schema(implementation = AluguelResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Locatário ou um ou mais livros não foram encontrados",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "Um ou mais livros solicitados já estão alugados e não podem ser reservados",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PostMapping
    ResponseEntity<AluguelResponseDTO> cadastrarAluguel(@Valid @RequestBody(required = true) AluguelRequestDTO request);
}
