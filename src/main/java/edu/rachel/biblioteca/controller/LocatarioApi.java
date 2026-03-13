package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
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
        name = "Locatários",
        description = "Operações para gerenciamento de locatários"
)
@RequestMapping(value = "/locatarios")
public interface LocatarioApi {
    
    @Operation(
            summary = "Cadastrar um novo locatário",
            description = "Recebe os dados de um locatário e retorna o locatário cadastrado"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Locatário cadastrado com sucesso",
            content = @Content(
                    schema = @Schema(implementation = LocatarioResponseDTO.class)
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
    ResponseEntity<LocatarioResponseDTO> cadastrarLocatario(@Valid @RequestBody(required = true) LocatarioRequestDTO request);

    @Operation(
            summary = "Buscar um locatário",
            description = "Busca um locatário pelo seu id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Locatário buscado com sucesso",
            content = @Content(
                    schema = @Schema(implementation = LocatarioResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Locatário não encontrado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/{id}")
    ResponseEntity<LocatarioResponseDTO> buscarLocatario(@PathVariable UUID id);
}