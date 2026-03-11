package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.AutorApi;
import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.service.AutorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
public class AutorController implements AutorApi {

    AutorService service;

    @Override
    public ResponseEntity<AutorResponseDTO> criarAutor(AutorRequestDTO request) {
        AutorResponseDTO response = service.criarAutor(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
