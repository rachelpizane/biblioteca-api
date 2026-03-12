package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.LivroApi;
import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;
import edu.rachel.biblioteca.service.LivroService;
import edu.rachel.biblioteca.utils.UriUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@AllArgsConstructor
@RestController
public class LivroController implements LivroApi {

    LivroService service;

    @Override
    public ResponseEntity<LivroResponseDTO> cadastrarLivro(LivroRequestDTO request) {
        LivroResponseDTO response = service.cadastrarLivro(request);
        URI location = UriUtils.construirLocation(response.id());

        return ResponseEntity.created(location).body(response);
    }
}
