package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.AutorApi;
import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.service.AutorService;
import edu.rachel.biblioteca.utils.UriUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class AutorController implements AutorApi {

    private final AutorService service;

    @Override
    public ResponseEntity<AutorResponseDTO> cadastrarAutor(AutorRequestDTO request) {
        AutorResponseDTO response = service.cadastrarAutor(request);
        URI location = UriUtils.construirLocation(response.id());

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<AutorResponseDTO> buscarAutor(UUID id) {
        AutorResponseDTO response = service.buscarAutor(id);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PageResponseDTO<AutorResumoDTO>> buscarAutores(String nome, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("nome").ascending()
        );

        PageResponseDTO<AutorResumoDTO> response = service.buscarAutores(nome, pageable);

        return ResponseEntity.ok(response);
    }
}
