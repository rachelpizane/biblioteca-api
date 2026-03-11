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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
        name = "Autores",
        description = "Operações para gerenciamento de autores"
)
@RequestMapping(
        value = "/autores",
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
)
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
}
