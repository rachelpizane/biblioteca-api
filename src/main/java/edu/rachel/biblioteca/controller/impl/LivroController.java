package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.LivroApi;
import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.StatusLivroEnum;
import edu.rachel.biblioteca.service.LivroService;
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
public class LivroController implements LivroApi {

    private final LivroService service;

    @Override
    public ResponseEntity<LivroResponseDTO> cadastrarLivro(LivroRequestDTO request) {
        LivroResponseDTO response = service.cadastrarLivro(request);
        URI location = UriUtils.construirLocation(response.id());

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<LivroResponseDTO> buscarLivro(UUID id) {
        LivroResponseDTO response = service.buscarLivro(id);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PageResponseDTO<LivroResumoDTO>> buscarLivros(StatusLivroEnum statusLivro, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("nome").ascending()
        );

        PageResponseDTO<LivroResumoDTO> response = service.buscarLivros(statusLivro, pageable);

        return ResponseEntity.ok(response);
    }
}
