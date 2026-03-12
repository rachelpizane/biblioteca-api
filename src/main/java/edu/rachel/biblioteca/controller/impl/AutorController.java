package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.AutorApi;
import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.service.AutorService;
import edu.rachel.biblioteca.utils.UriUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class AutorController implements AutorApi {

    AutorService service;

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
}
