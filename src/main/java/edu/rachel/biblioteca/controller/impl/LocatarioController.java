package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.LocatarioApi;
import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.service.LocatarioService;
import edu.rachel.biblioteca.utils.UriUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class LocatarioController implements LocatarioApi {

    private final LocatarioService service;

    @Override
    public ResponseEntity<LocatarioResponseDTO> cadastrarLocatario(LocatarioRequestDTO request) {
        LocatarioResponseDTO response = service.cadastrarLocatario(request);
        URI location = UriUtils.construirLocation(response.id());

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<LocatarioResponseDTO> buscarLocatario(UUID id) {
        LocatarioResponseDTO response = service.buscarLocatario(id);

        return ResponseEntity.ok(response);
    }
}