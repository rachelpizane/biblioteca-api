package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.StatusLivroEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Tag(
        name = "Livros",
        description = "Operações para gerenciamento de livros"
)
@RequestMapping(value = "/livros")
public interface LivroApi {

    @Operation(
            summary = "Cadastrar um novo livro",
            description = "Recebe os dados de um livro e retorna o livro cadastrado"
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
            description = "Um ou mais autores não foram encontrados",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PostMapping
    ResponseEntity<LivroResponseDTO> cadastrarLivro(@Valid @RequestBody(required = true) LivroRequestDTO request);

    @Operation(
            summary = "Buscar um livro",
            description = "Busca um livro pelo seu id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Livro buscado com sucesso",
            content = @Content(
                    schema = @Schema(implementation = LivroResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/{id}")
    ResponseEntity<LivroResponseDTO> buscarLivro(@PathVariable UUID id);

    @Operation(
            summary = "Buscar livros",
            description = "Retorna uma lista paginada de livros com opção de filtro por status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pagina com os livros filtrados com sucesso",
            content = @Content(
                    schema = @Schema(implementation = PageResponseDTO.class)
            )
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO<LivroResumoDTO>> buscarLivros(
            @Parameter(description = "Status do livro", example = "DISPONIVEL")
            @RequestParam(name = "status", required = false) StatusLivroEnum statusLivro,

            @Parameter(description = "Número da página", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de livros por página", example = "5")
            @RequestParam(defaultValue = "5") int size
    );
}
