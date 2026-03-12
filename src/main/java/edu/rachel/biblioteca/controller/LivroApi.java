package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Livros",
        description = "Operações para gerenciamento de livros"
)
@RequestMapping(value = "/livros")
public interface LivroApi {

    @Operation(
            summary = "Cadastrar um novo livro",
            description = "Recebe os dados de um livro e retorna o livro cadastrado."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Livro cadastrado com sucesso",
            content = @Content(
                    schema = @Schema(implementation = LivroResponseDTO.class)
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
            description = "Autores não encontrados",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PostMapping
    ResponseEntity<LivroResponseDTO> cadastrarLivro(@Valid @RequestBody(required = true) LivroRequestDTO request);
}
