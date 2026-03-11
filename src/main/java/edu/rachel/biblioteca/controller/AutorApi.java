package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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
            summary = "Cria um novo autor",
            description = "Recebe os dados de um autor e retorna o autor criado."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Autor criado com sucesso",
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
    ResponseEntity<AutorResponseDTO> criarAutor(@Valid @RequestBody(required = true) AutorRequestDTO request);

    @Operation(
            summary = "Busca um autor",
            description = "Busca um autor pelo seu id."
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
}
