package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.dto.*;
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
        name = "Autores",
        description = "Operações para gerenciamento de autores"
)
@RequestMapping(value = "/autores")
public interface AutorApi {

    @Operation(
            summary = "Cadastrar um novo autor",
            description = "Recebe os dados de um autor e retorna o autor cadastrado"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Autor cadastrado com sucesso",
            content = @Content(
                    schema = @Schema(implementation = AutorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PostMapping
    ResponseEntity<AutorResponseDTO> cadastrarAutor(@Valid @RequestBody(required = true) AutorRequestDTO request);

    @Operation(
            summary = "Buscar um autor",
            description = "Busca um autor pelo seu id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Autor buscado com sucesso",
            content = @Content(
                    schema = @Schema(implementation = AutorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Autor não encontrado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/{id}")
    ResponseEntity<AutorResponseDTO> buscarAutor(@PathVariable UUID id);

    @Operation(
            summary = "Buscar autores",
            description = "Retorna uma lista paginada de autores com opção de filtro por nome"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pagina com os autores filtrados com sucesso",
            content = @Content(
                    schema = @Schema(implementation = PageResponseDTO.class)
            )
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO<AutorResumoDTO>> buscarAutores(
            @Parameter(description = "Nome do autor", example = "Carlos")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Número da página", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de autores por página", example = "5")
            @RequestParam(defaultValue = "5") int size
    );
}
